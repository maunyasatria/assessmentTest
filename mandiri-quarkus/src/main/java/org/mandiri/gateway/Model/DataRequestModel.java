package org.mandiri.gateway.Model;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.GenericGenerator;
import org.mandiri.gateway.Util.GlobalFunction;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "data_request",
        schema = "data_schema"
)
public class DataRequestModel extends PanacheEntityBase {
    @Id
    @Column(name = "request_id", length = 36, nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    public String requestId;

    @Column(name = "person_cif", length = 36, nullable = false)
    public String personCif;

    @Column(name = "invoice_id", length = 36, nullable = false)
    public String invoiceId;

    @Column(name = "invoice_type", length = 36, nullable = false)
    public String invoiceType;

    @Column(name = "product_code", length = 50, nullable = false)
    public String productCode = "";

    @Column(name = "amount", nullable = false)
    public Float amount = (float) 0.0;

    @Column(name = "initiator", length = 10, nullable = false)
    public String initiator;

    @Column(name = "request_status", length = 10, nullable = false)
    public String requestStatus;

    @Column(name = "created_at", nullable = false)
    public Timestamp createdAt = GlobalFunction.defaultTime();

    @Column(name = "created_by", length = 40, nullable = false)
    public String createdBy = "";

    @Column(name = "updated_at", nullable = false)
    public Timestamp updatedAt = GlobalFunction.defaultTime();

    @Column(name = "updated_by", length = 40, nullable = false)
    public String updatedBy = "";
}
