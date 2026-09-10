package com.project.pdm.notification.notification_service.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "sensors", schema = "prediction_schema")
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String type;

    private String unit;

    private Instant calibrationDate;

    @ManyToOne
    @JoinColumn(
            name = "device_id",
            referencedColumnName = "id"
    )
    private Device device;
}
