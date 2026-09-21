from datetime import datetime, timezone
import logging

import grpc
from google.protobuf import timestamp_pb2

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
        response.calculate_at = timestamp_pb2.Timestamp(seconds = int(datetime.now(timezone.utc).timestamp()))
        response.rul_hours = 22.2
        response.confidence = 0.50
        response.risk_score = 1.0
        response.source = PredictionService_pb2.PredictionSource.PREDICTION_SOURCE_UNSPECIFIED
        response.maintenance_recommendation = "TETS MAINTENANCE"

        return response
