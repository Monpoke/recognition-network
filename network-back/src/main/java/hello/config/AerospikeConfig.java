package hello.config;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.policy.ClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 *
 * @author Peter Milne
 * @author Jean Mercier
 *
 *//*
@Configuration
@EnableAerospikeRepositories(basePackages = {"hello.aerospike.repositories"})
@EnableAutoConfiguration
@EnableTransactionManagement
public class AerospikeConfig {

    private static final Logger log = LoggerFactory.getLogger(AerospikeConfig.class);

    public @Bean(destroyMethod = "close")
    AerospikeClient aerospikeClient() {

        ClientPolicy policy = new ClientPolicy();
        policy.failIfNotConnected = true;

        log.info("Connecting to Aerospike..");

        return new AerospikeClient(policy, "localhost", 3000);
    }

    public @Bean
    AerospikeTemplate aerospikeTemplate() {
        return new AerospikeTemplate(aerospikeClient(), "loadAllRefs"); // TODO verify correct place for namespace
    }

}*/