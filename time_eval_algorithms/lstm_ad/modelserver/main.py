from multiprocessing import AuthenticationError
from typing import Optional

import numpy as np
from fastapi import Depends, FastAPI, HTTPException, Security
from fastapi.security.api_key import APIKeyHeader
from pydantic import BaseModel
from starlette.status import HTTP_400_BAD_REQUEST, HTTP_401_UNAUTHORIZED

class WindowFeatures(BaseModel):
    data: list

    def to_numpy(self):
        return np.array(self.data).astype(np.float32)

class PredictionResult(BaseModel):
    predicted: float

def make_app(model, api_token=None):
    app = FastAPI()

    api_key = APIKeyHeader(name="token", auto_error=False)

    def validate_request(header: Optional[str] = Security(api_key)) -> bool:
        if api_token is not None:
            if header is None:
                raise HTTPException(
                    status_code=HTTP_400_BAD_REQUEST, detail="API key is missing", headers={}
                )
            if header != api_token:
                raise HTTPException(
                    status_code=HTTP_401_UNAUTHORIZED, detail="Access not allowed", headers={}
                )
            return True
        
        return True

    @app.get("/status")
    def get_status():
        return {"status": "ok"}

    @app.post("/predict", response_model=PredictionResult)
    def post_predict(features: WindowFeatures, authenticated: bool = Depends(validate_request)
    ):
        assert authenticated == True
        score = model.anomaly_detection(features.to_numpy())
        return PredictionResult(predicted=float(score[0]))
    
    return app
