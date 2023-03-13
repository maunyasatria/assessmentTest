package org.mandiri.gateway.Repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.mandiri.gateway.Model.DataModel;
import org.mandiri.gateway.Model.DataRequestModel;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class DataRepository {

    public JsonObject getDataList(String queryString, int page, int rows, Parameters params) {
        Sort sort = Sort.descending("payment_id");
        PanacheQuery<DataModel> query = DataModel
                .find(queryString, sort, params)
                .page(page, rows);

        List<DataModel> list = query.list();
        JsonArray dataList = new JsonArray();

        for (int i = 0; i < list.size(); i++) {
            DataRequestModel dataRequestModel = DataRequestModel.findById(list.get(i).requestId);
            dataList.add(JsonObject.mapFrom(list.get(i)));
            dataList.getJsonObject(i).remove("request");
            dataList.getJsonObject(i).remove("response");

            dataList.getJsonObject(i).put("createdAt", list.get(i).createdAt.toString());
            dataList.getJsonObject(i).put("updatedAt", list.get(i).updatedAt.toString());

        }

        return new JsonObject().put("maxPage", query.pageCount())
                .put("totalData", query.count())
                .put("data", dataList);
    }

    public JsonObject detailData(String paymentId) {
        DataModel dataModel = DataModel.findById(paymentId);
        JsonObject detail = new JsonObject();
        if (null != dataModel) {
            detail = JsonObject.mapFrom(dataModel);
            detail.remove("request");
            detail.remove("response");
        }
        return detail;
    }
}
