package org.mandiri.gateway.Kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.Node;

import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaAdminClient {
    private final AdminClient client;

    public KafkaAdminClient(String bootstrap) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrap);
        props.put("default.api.timeout.ms", 3000);
        props.put("request.timeout.ms", 3000);
        props.put("connections.max.idle.ms", 5000);
        props.put("retries", 3);

        this.client = AdminClient.create(props);
    }

    public boolean verifyConnection() throws ExecutionException, InterruptedException {
        Collection<Node> nodes = this.client.describeCluster()
                .nodes()
                .get();
        return nodes != null && nodes.size() > 0;
    }

    public boolean verifyConnectionMain() {
        boolean isKafkaActive;
        try {
            isKafkaActive = verifyConnection();
        } catch (ExecutionException e) {
            isKafkaActive = false;
        } catch (InterruptedException e) {
            isKafkaActive = false;
        }
        finally {
            this.client.close();
        }

        return isKafkaActive;
    }
}
