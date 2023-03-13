package org.mandiri.gateway.Model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.mandiri.gateway.Util.GlobalFunction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "data",
        schema = "data_schema"
)
public class DataModel extends PanacheEntityBase {
    @Id
    @Column(name = "data_id", length = 20, nullable = false)
    public String paymentId;

    @Column(name = "request_id", length = 36, nullable = false)
    public String requestId;

    @Column(name = "invoice_id", length = 36, nullable = false)
    public String invoiceId ="";

    @Column(name = "status", length = 10, nullable = false)
    public String status;

    @Column(name = "amount", nullable = false)
    public Float amount = (float) 0.0;

    @Column(name = "fail_status", length = 10, nullable = false)
    public String failStatus = "";


    @Column(columnDefinition = "text default ''", name = "request", nullable = false)
    public String request = "";

    @Column(columnDefinition = "text default ''", name = "response", nullable = false)
    public String response = "";

    @Column(name = "data_type", length = 20, nullable = false)
    public String dataType;

    @Column(name = "created_at", nullable = false)
    public Timestamp createdAt = GlobalFunction.defaultTime();

    @Column(name = "created_by",  length = 40, nullable = false)
    public String createdBy;

    @Column(name = "updated_at", nullable = false)
    public Timestamp updatedAt = GlobalFunction.defaultTime();

    @Column(name = "updated_by", length = 40, nullable = false)
    public String updatedBy;

}
