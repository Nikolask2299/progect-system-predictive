import datetime

from google.protobuf import timestamp_pb2 as _timestamp_pb2
from google.protobuf.internal import containers as _containers
from google.protobuf.internal import enum_type_wrapper as _enum_type_wrapper
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from collections.abc import Iterable as _Iterable, Mapping as _Mapping
from typing import ClassVar as _ClassVar, Optional as _Optional, Union as _Union

DESCRIPTOR: _descriptor.FileDescriptor

class PredictionSource(int, metaclass=_enum_type_wrapper.EnumTypeWrapper):
    __slots__ = ()
    PREDICTION_SOURCE_ACTUAL: _ClassVar[PredictionSource]
    PREDICTION_SOURCE_CACHED: _ClassVar[PredictionSource]
    PREDICTION_SOURCE_UNSPECIFIED: _ClassVar[PredictionSource]
PREDICTION_SOURCE_ACTUAL: PredictionSource
PREDICTION_SOURCE_CACHED: PredictionSource
PREDICTION_SOURCE_UNSPECIFIED: PredictionSource

class PredictionResponse(_message.Message):
    __slots__ = ("device_id", "calculate_at", "rul_hours", "confidence", "risk_score", "source", "maintenance_recommendation")
    DEVICE_ID_FIELD_NUMBER: _ClassVar[int]
    CALCULATE_AT_FIELD_NUMBER: _ClassVar[int]
    RUL_HOURS_FIELD_NUMBER: _ClassVar[int]
    CONFIDENCE_FIELD_NUMBER: _ClassVar[int]
    RISK_SCORE_FIELD_NUMBER: _ClassVar[int]
    SOURCE_FIELD_NUMBER: _ClassVar[int]
    MAINTENANCE_RECOMMENDATION_FIELD_NUMBER: _ClassVar[int]
    device_id: int
    calculate_at: _timestamp_pb2.Timestamp
    rul_hours: float
    confidence: float
    risk_score: float
    source: PredictionSource
    maintenance_recommendation: str
    def __init__(self, device_id: _Optional[int] = ..., calculate_at: _Optional[_Union[datetime.datetime, _timestamp_pb2.Timestamp, _Mapping]] = ..., rul_hours: _Optional[float] = ..., confidence: _Optional[float] = ..., risk_score: _Optional[float] = ..., source: _Optional[_Union[PredictionSource, str]] = ..., maintenance_recommendation: _Optional[str] = ...) -> None: ...

class PredictionRequest(_message.Message):
    __slots__ = ("device_id", "window_start", "window_end", "sensors")
    DEVICE_ID_FIELD_NUMBER: _ClassVar[int]
    WINDOW_START_FIELD_NUMBER: _ClassVar[int]
    WINDOW_END_FIELD_NUMBER: _ClassVar[int]
    SENSORS_FIELD_NUMBER: _ClassVar[int]
    device_id: int
    window_start: _timestamp_pb2.Timestamp
    window_end: _timestamp_pb2.Timestamp
    sensors: _containers.RepeatedCompositeFieldContainer[SensorAggregation]
    def __init__(self, device_id: _Optional[int] = ..., window_start: _Optional[_Union[datetime.datetime, _timestamp_pb2.Timestamp, _Mapping]] = ..., window_end: _Optional[_Union[datetime.datetime, _timestamp_pb2.Timestamp, _Mapping]] = ..., sensors: _Optional[_Iterable[_Union[SensorAggregation, _Mapping]]] = ...) -> None: ...

class SensorAggregation(_message.Message):
    __slots__ = ("sensor_id", "timestamp", "count", "sum", "mean", "max", "min", "std_dev")
    SENSOR_ID_FIELD_NUMBER: _ClassVar[int]
    TIMESTAMP_FIELD_NUMBER: _ClassVar[int]
    COUNT_FIELD_NUMBER: _ClassVar[int]
    SUM_FIELD_NUMBER: _ClassVar[int]
    MEAN_FIELD_NUMBER: _ClassVar[int]
    MAX_FIELD_NUMBER: _ClassVar[int]
    MIN_FIELD_NUMBER: _ClassVar[int]
    STD_DEV_FIELD_NUMBER: _ClassVar[int]
    sensor_id: int
    timestamp: _timestamp_pb2.Timestamp
    count: int
    sum: float
    mean: float
    max: float
    min: float
    std_dev: float
    def __init__(self, sensor_id: _Optional[int] = ..., timestamp: _Optional[_Union[datetime.datetime, _timestamp_pb2.Timestamp, _Mapping]] = ..., count: _Optional[int] = ..., sum: _Optional[float] = ..., mean: _Optional[float] = ..., max: _Optional[float] = ..., min: _Optional[float] = ..., std_dev: _Optional[float] = ...) -> None: ...
