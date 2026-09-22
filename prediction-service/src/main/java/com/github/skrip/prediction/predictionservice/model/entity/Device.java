package com.github.skrip.prediction.predictionservice.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "devices", schema = "prediction_schema")
@AllArgsConstructor
@NoArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String type;

    private String locationId;

    private Instant installDate;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private List<PredictionAggregation> predictionAggregations;
}
