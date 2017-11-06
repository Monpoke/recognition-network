package hello.aerospike.repositories;

import hello.aerospike.domain.RefImage;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface RefImageRepository extends AerospikeRepository<RefImage,Integer> {
}
