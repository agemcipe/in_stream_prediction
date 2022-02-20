import argparse
import json
import sys
from dataclasses import asdict, dataclass, field
from typing import List

import numpy as np
import pandas as pd
import torch
import uvicorn

from uvicorn.config import LOGGING_CONFIG

from lstm_ad.model import LSTMAD
from modelserver.main import make_app


@dataclass
class CustomParameters:
    lstm_layers: int = 2
    split: float = 0.9
    window_size: int = 30
    prediction_window_size: int = 1
    output_dims: List[int] = field(default_factory=lambda: [])
    batch_size: int = 32
    validation_batch_size: int = 128
    test_batch_size: int = 128
    epochs: int = 50  # bigger for smaller datasets, smaller for bigger datasets
    early_stopping_delta: float = 0.05
    early_stopping_patience: int = 10
    optimizer: str = "adam"  # not exposed, always use Adam!
    learning_rate: float = 1e-3
    random_state: int = 42


class AlgorithmArgs(argparse.Namespace):
    @property
    def ts(self) -> np.ndarray:
        return self.df.iloc[:, 1:-1].values

    @property
    def df(self) -> pd.DataFrame:
        return pd.read_csv(self.dataInput)

    @staticmethod
    def from_sys_args() -> 'AlgorithmArgs':
        args: dict = json.loads(sys.argv[1])
        custom_parameter_keys = dir(CustomParameters())
        filtered_parameters = dict(
            filter(lambda x: x[0] in custom_parameter_keys, args.get("customParameters", {}).items()))
        args["customParameters"] = CustomParameters(**filtered_parameters)
        return AlgorithmArgs(**args)


def train(args: AlgorithmArgs):
    data = args.ts
    model = LSTMAD(input_size=data.shape[1], **asdict(args.customParameters))
    model.fit(data, args.modelOutput)
    model.save(args.modelOutput)


def execute(args: AlgorithmArgs):
    data = args.ts
    model = LSTMAD.load(
        args.modelInput, input_size=data.shape[1], **asdict(args.customParameters))
    anomaly_scores = model.anomaly_detection(data)
    anomaly_scores.tofile(args.dataOutput, sep="\n")

def run_prediction_server(args: AlgorithmArgs):
    
    model = LSTMAD.load(
        args.modelInput, input_size=args.input_size, **asdict(args.customParameters)
        )
    
    LOGGING_CONFIG["formatters"]["default"]["fmt"] = "%(asctime)s %(levelprefix)s %(message)s"
    app = make_app(model, api_token=None) # TODO: pass api_token with args
    uvicorn.run(app, **{"host": "0.0.0.0", "port": 8000})


def set_random_state(config: AlgorithmArgs) -> None:
    seed = config.customParameters.random_state
    import random
    random.seed(seed)
    np.random.seed(seed)
    torch.manual_seed(seed)


if __name__ == "__main__":
    args = AlgorithmArgs.from_sys_args()
    set_random_state(args)

    if args.executionType == "train":
        train(args)
    elif args.executionType == "execute":
        execute(args)
    elif args.executionType == "run_prediction_server":
        run_prediction_server(args)
    else:
        raise ValueError(
            f"No executionType '{args.executionType}' available! Choose either 'train' or 'execute'.")
