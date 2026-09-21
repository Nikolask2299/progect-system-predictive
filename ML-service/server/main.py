import logging
import os
from concurrent import futures
import signal

import sys; print(sys.path)

sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'generated'))

import grpc

from generated import PredictionService_pb2_grpc
from grpcService.server import PredictionServiceImpl

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

def serve():
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
