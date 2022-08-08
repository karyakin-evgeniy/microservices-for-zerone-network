package org.proteam24.zeroneapplication.repository.jooq;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import org.proteam24.zeroneapplication.entity.PostEntity;
import org.proteam24.zeroneapplication.entity.Tag2PostEntity;
import org.proteam24.zeroneapplication.entity.TagEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static org.jooq.impl.DSL.*;

@Slf4j
@Repository
public class PostJooqRepository extends JooqRepository {

    private final EntityManager entityManager;
    Table<Record> tablePost;
    Table<Record> tableUsers;
    Table<Record> tableTag;
    Table<Record> tableTag2Post;

    public PostJooqRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.tablePost = table(getTableName(entityManager, PostEntity.class));
        this.tableUsers = table(getTableName(entityManager, UserEntity.class));
        this.tableTag = table(getTableName(entityManager, TagEntity.class));
        this.tableTag2Post = table(getTableName(entityManager, Tag2PostEntity.class));
    }

    public List<PostEntity> getCountTotal(String dateFrom, String dateTo, String text, String author, String tag) {
        int countTags = StringUtils.countMatches(tag, "_");
        Condition condition = getCondition(dateFrom, dateTo, text, author, tag);
        org.jooq.Query jooqQuery = DSL.select(field("*"), count())
                .from(tablePost)
                .join(tableUsers)
                .on(field(tablePost.getName() + ".author_id").eq(field(tableUsers.getName() + ".id")))
                .leftJoin(tableTag2Post)
                .on(field(tablePost.getName() + ".id").eq(field(tableTag2Post.getName() + ".post_id")))
                .leftJoin(tableTag)
                .on(field(tableTag2Post.getName() + ".tag_id").eq(field(tableTag.getName() + ".id")))
                .where(condition).groupBy(field("post.id")).having(count().gt(countTags))
                ;
        return execQuery(entityManager, jooqQuery, PostEntity.class);
    }

    public List<PostEntity> getPostsByText(String dateFrom, String dateTo, String text, String author, String tag, Integer itemPerPage, Integer offset) {
        int countTags = StringUtils.countMatches(tag, "_");
        Condition condition = getCondition(dateFrom, dateTo, text, author, tag);
        org.jooq.Query jooqQuery = DSL.select(field("*"), count())
                .from(tablePost)
                .join(tableUsers)
                .on(field(tablePost.getName() + ".author_id").eq(field(tableUsers.getName() + ".id")))
                .leftJoin(tableTag2Post)
                .on(field(tablePost.getName() + ".id").eq(field(tableTag2Post.getName() + ".post_id")))
                .leftJoin(tableTag)
                .on(field(tableTag2Post.getName() + ".tag_id").eq(field(tableTag.getName() + ".id")))
                .where(condition).groupBy(field("post.id")).having(count().gt(countTags))
                .orderBy(field("time").desc())
                .limit(itemPerPage)
                .offset(offset)
                ;
        return execQuery(entityManager, jooqQuery, PostEntity.class);
    }

    private Condition getCondition(String dateFrom, String dateTo, String text, String author, String tag) {
        return makeMoreLessTimeCondition("time", dateFrom, dateTo)
                .and(makeLikeCondition("post_text", text).or(makeLikeCondition("title", text)))
                .and(field("is_deleted").eq(Boolean.FALSE))
                .and(makeLikeCondition(tableUsers.getName() + ".first_name", author)
                        .or(makeLikeCondition(tableUsers.getName() + ".last_name", author)))
                .and(makeLikeCondition(tableTag.getName() + ".name", tag))
                ;
    }
}
