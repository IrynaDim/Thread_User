package com.dev.thread.user.dao.mongo;

import com.dev.thread.user.exception.SequenceException;
import com.dev.thread.user.model.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class SequenceDao {
    private final MongoOperations userRepository;

    @Autowired
    public SequenceDao(MongoOperations mongoOperations) {
        this.userRepository = mongoOperations;
    }

    public Long getNextSequenceId(String key) {
        // получаем объект Sequence по наименованию коллекции
        Query query = new Query(Criteria.where("id").is(key));

        // увеличиваем поле sequence на единицу
        Update update = new Update();
        update.inc("sequence", 1);

        // указываем опцию, что нужно возвращать измененный объект
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);

        // немного магии :)
        Sequence sequence = userRepository.findAndModify(query, update, options, Sequence.class);

        // если не нашли такой sequence, выбросить исключение
        if(sequence == null) throw new SequenceException("Unable to get sequence for key: " + key);

        return sequence.getValue();
    }
}
