import logging
from datetime import datetime, timezone
import random

import grpc

from generated import PredictionService_pb2_grpc, PredictionService_pb2

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

class PredictionServiceImpl(PredictionService_pb2_grpc.PredictionServiceServicer):

    def GetPrediction(self, request, context):
        if request.device_id is None:
           context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
           context.set_details("Device ID must be positive")
           return PredictionService_pb2.PredictionResponse()

        logger.info(f"GetPrediction: device_id={request.device_id}")

        response = PredictionService_pb2.PredictionResponse()
        response.device_id = request.device_id
        response.calculate_at.FromDatetime(datetime.now(timezone.utc))
        response.rul_hours = random.random() * 10
        response.confidence = 0.50
        response.risk_score = 1.0
        response.source = PredictionService_pb2.PredictionSource.ACTUAL
        response.maintenance_recommendation = "TETS MAINTENANCE"

        return response
