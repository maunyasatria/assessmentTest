package org.mandiri.gateway.Service;


import io.quarkus.panache.common.Parameters;
import io.quarkus.runtime.util.ExceptionUtil;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.tuple.Pair;
import org.mandiri.gateway.Constant.ConstantVariable;
import org.mandiri.gateway.ErrorList.ErrorListConstant;
import org.mandiri.gateway.Exception.ValidationApiException;
import org.mandiri.gateway.Repository.DataRepository;
import org.mandiri.gateway.Util.GlobalFunction;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ListData {


    @Inject
    DataRepository dataRepository;

    public JsonObject getDataList(UriInfo uriInfo, JsonObject filter) throws ValidationApiException {
        LocalDateTime reqAt = LocalDateTime.now();

        // validate strings
        ArrayList<String> listValidate = new ArrayList<>();
        listValidate.add("dataId");
        listValidate.add("requestDateMin");
        listValidate.add("requestDateMax");

        //ambil halaman yang akan ditampilkan
        int page;
        try {
            if (filter.containsKey("page") && filter.getInteger("page") != null && filter.getInteger("page") >= 0)
                page = filter.getInteger("page");
            else page = 0;
        } catch (ClassCastException cce) {
            page = 0;
        }

        if (filter.size() == 0) {
            //get all
            Parameters params = new Parameters();
            params.and("dataId", filter.getString("dataId"));
            return dataRepository.getDataList("SELECT p FROM dataModel p JOIN dataRequestModel pr ON p.requestId = pr.requestId WHERE pr.dataId = :dataId", page, filter.getInteger("row"), params);
        } else {
            //get filters
            Pair<List<String>, Parameters> filterListString = fetchFilterListString(filter, uriInfo, reqAt);
            List<String> filterListFloat = fetchFilterListFloat(filter, uriInfo, reqAt);

            filterListString.getRight().and("dataId", filter.getString("dataId"));

            return dataRepository.getDataList(craftDynamicQuery(filter.getString("dataId"), filterListString.getLeft(), filterListFloat), page, filter.getInteger("row"), filterListString.getRight());
        }
    }

    private Pair<List<String>, Parameters> fetchFilterListString(JsonObject filter, UriInfo uriInfo, LocalDateTime reqAt) throws ValidationApiException {
        List<String> filterList = new ArrayList<>();
        String keyCheck;
        String keyCheck2;

        Parameters params = new Parameters();

        keyCheck = "invoiceId";
        if (filter.containsKey(keyCheck) && !GlobalFunction.isNullOrBlankOne(filter.getString(keyCheck))) {
            filterList.add("LOWER(p." + keyCheck + ") LIKE LOWER(:" + keyCheck + ")");
            params.and(keyCheck, "%" + filter.getString(keyCheck) + "%");
        }
        keyCheck = "dataStatus";
        if (filter.containsKey(keyCheck) && !GlobalFunction.isNullOrBlankOne(filter.getString(keyCheck))) {
            filterList.add("LOWER(p." + keyCheck + ") LIKE LOWER(:" + keyCheck + ")");
            params.and(keyCheck, "%" + filter.getString(keyCheck) + "%");
        }
        keyCheck = "dataType";
        if (filter.containsKey(keyCheck) && !GlobalFunction.isNullOrBlankOne(filter.getString(keyCheck))) {
            filterList.add("LOWER(p." + keyCheck + ") LIKE LOWER(:" + keyCheck + ")");
            params.and(keyCheck, "%" + filter.getString(keyCheck) + "%");
        }
        keyCheck = "dataId";
        if (filter.containsKey(keyCheck) && !GlobalFunction.isNullOrBlankOne(filter.getString(keyCheck))) {
            filterList.add("LOWER(p." + keyCheck + ") LIKE LOWER(:" + keyCheck + ")");
            params.and(keyCheck, "%" + filter.getString(keyCheck) + "%");
        }
        keyCheck = "requestDateMin";
        keyCheck2 = "requestDateMax";
        if (filter.containsKey(keyCheck) && !GlobalFunction.isNullOrBlankOne(filter.getString(keyCheck))) {
            if (filter.containsKey(keyCheck2) && !GlobalFunction.isNullOrBlankOne(filter.getString(keyCheck2))) {
                if (Timestamp.valueOf(filter.getString(keyCheck2)).compareTo(Timestamp.valueOf(filter.getString(keyCheck))) < 0)

                filterList.add("p.createdAt = '" + filter.getString(keyCheck) + "'");
            }
        }
        return Pair.of(filterList, params);
    }

    private List<String> fetchFilterListFloat(JsonObject filter, UriInfo uriInfo, LocalDateTime reqAt) throws ValidationApiException {
        List<String> filterList = new ArrayList<>();
        String keyCheck;
        String keyCheck2;

        try {
            keyCheck = "amountMin";
            keyCheck2 = "amountMax";
            if (filter.containsKey(keyCheck) && !GlobalFunction.isNullOrBlankOne(filter.getFloat(keyCheck)) && filter.getFloat(keyCheck) >= 0) {
                if (filter.containsKey(keyCheck2) && !GlobalFunction.isNullOrBlankOne(filter.getFloat(keyCheck2)) && filter.getFloat(keyCheck2) >= 0) {
                    if (filter.getFloat(keyCheck2) < filter.getFloat(keyCheck))
                        throw (ValidationApiException) ExceptionUtil.ExceptionExceptionFormatter(uriInfo.getPath(), reqAt, filter,
                                ErrorListConstant.ERROR_INVALID_RANGE, null, ValidationApiException.class);
                    filterList.add("p.amount BETWEEN " + filter.getFloat(keyCheck) + " AND " + filter.getFloat(keyCheck2));
                } else
                    filterList.add("p.amount >= " + filter.getFloat(keyCheck));
            } else {
                if (filter.containsKey(keyCheck2) && !GlobalFunction.isNullOrBlankOne(filter.getFloat(keyCheck2)) && filter.getFloat(keyCheck2) >= 0)
                    filterList.add("p.amount <= " + filter.getFloat(keyCheck2));
            }
        } catch (ClassCastException cce) {
            throw (ValidationApiException) ExceptionUtil.ExceptionExceptionFormatter(uriInfo.getPath(), reqAt, filter,
                    ErrorListConstant.WRONG_VALUE_FIELD, null, ValidationApiException.class);
        }

        return filterList;
    }


    private String craftDynamicQuery(String dataId, List<String> listString, List<String> listFloat) {
        String query = "SELECT p FROM dataModel p JOIN dataRequestModel pr ON p.requestId = pr.requestId WHERE pr.dataId = :dataId";

        if (listString.size() > 0)
            query += " AND ";

        for (int i = 0; i < listString.size(); i++) {
            query += listString.get(i);

            if (i < listString.size() - 1)
                query += " AND ";
        }

        if (listFloat.size() > 0)
            query += " AND ";

        for (int i = 0; i < listFloat.size(); i++) {
            query += listFloat.get(i);

            if (i < listFloat.size() - 1)
                query += " AND ";
        }

        return query;
    }

    public JsonObject dataStatus() {
        JsonObject status = new JsonObject();
        status.put("success", ConstantVariable.DATA_STATUS_SUCCESS);
        status.put("failed", ConstantVariable.DATA_STATUS_FAILED);
        status.put("waiting", ConstantVariable.DATA_STATUS_WAITING);
        return status;
    }

}