import logging
import signal
import sys
from concurrent import futures

print(sys.path)

import grpc

import py_eureka_client.eureka_client as eureka_client
import os

from generated import PredictionService_pb2_grpc
from grpcService.server import PredictionServiceImpl

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

def serve():
    eureka_client.init(
        eureka_server=os.getenv("EUREKA_SERVER_URL", "http://eureka-server:8761/eureka/"),
        app_name=os.getenv("EUREKA_APP_NAME", "ml-service"),
        instance_host=os.getenv("EUREKA_INSTANCE_HOST", "ml-service.pdm.local"),
        instance_port=int(os.getenv("EUREKA_INSTANCE_PORT", 50050)))

    server = grpc.server(
        futures.ThreadPoolExecutor(max_workers=100),
        options=[
            ('grpc.max_concurrent_streams', 100),
            ('grpc.so_reuseport', 1)
        ]
    )

    PredictionService_pb2_grpc.add_PredictionServiceServicer_to_server(PredictionServiceImpl(), server)

    port = server.add_insecure_port('[::]:50050')
    server.start()
    logger.info(f"ML SERVICE STARTED: {port}")

    def shutdown(signum, frame):
        logger.info("SHUTTING DOWN GRACEFULLY...")
        server.stop(grace=30)

    signal.signal(signal.SIGINT, shutdown)
    signal.signal(signal.SIGTERM, shutdown)

    server.wait_for_termination()

if __name__ == '__main__':
    serve()
