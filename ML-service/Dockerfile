FROM python:3.14-slim
LABEL authors="Nikolas"

ENV PYTHONDONTWRITEBYTECODE=1 PYTHONUNBUFFERED=1

WORKDIR /app

RUN pip install grpcio
RUN pip install grpcio-tools
RUN pip install py-eureka-client

COPY . .

EXPOSE 50050

ENTRYPOINT ["python", "main.py"]