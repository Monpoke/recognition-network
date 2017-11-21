package hello.aerospike.repositories;

import hello.aerospike.domain.RefImageMetadata;
import org.springframework.data.repository.CrudRepository;

public interface RefImageMetadataRepository extends CrudRepository<RefImageMetadata,Integer> {
}
