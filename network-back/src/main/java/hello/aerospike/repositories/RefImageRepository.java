package hello.aerospike.repositories;

import hello.aerospike.domain.RefImage;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.data.repository.CrudRepository;

public interface RefImageRepository extends CrudRepository<RefImage,Integer> {
    RefImage findById(String id);
}
