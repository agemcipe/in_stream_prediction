FROM mut:5000/akita/python3-torch:0.2.1

LABEL maintainer="phillip.wenig@hpi.de"

ENV ALGORITHM_MAIN="/app/algorithm.py"


# install algorithm dependencies
COPY requirements.txt /app/
RUN pip install -r /app/requirements.txt

COPY lstm_ad /app/lstm_ad
COPY algorithm.py /app/

# for model serving
COPY modelserver /app/modelserver
EXPOSE 8000