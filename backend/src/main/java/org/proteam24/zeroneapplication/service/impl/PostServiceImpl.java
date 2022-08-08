package org.proteam24.zeroneapplication.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.proteam24.zeroneapplication.dto.*;
import org.proteam24.zeroneapplication.entity.*;
import org.proteam24.zeroneapplication.repository.*;
import org.proteam24.zeroneapplication.service.CommentsService;
import org.proteam24.zeroneapplication.service.PostService;
import org.proteam24.zeroneapplication.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final TagEntityRepository tagEntityRepository;
    private final Tag2PostEntityRepository tag2PostEntityRepository;
    private final UserService userService;
    private final CommentsService commentsService;
    private final CommentsRepository commentsRepository;
    private final LikesRepository likesRepository;
    private final FileRepository fileRepository;

    @Value("${scheduler.delete.post.day}")
    private Integer daysBeforeDeleted;


    @Override
    public BaseResponseSomeDto<List<PostDto>> getSomePosts(int offset, int perPage, String email) {
        UserEntity userEntity = userService.findByEmail(email);
        long total = postRepository.count();
        Pageable pageable = PageRequest.of(offset /= perPage, perPage);
        Page<PostEntity> posts = postRepository.getSomePosts(pageable);
        List<PostEntity> result = new ArrayList<>();

        posts.forEach(post -> {
            if (LocalDateTime.now().isAfter(post.getTime())) {
                result.add(post);
            }
        });

        List<PostDto> postDtoList = new ArrayList<>();

        for (PostEntity postEntity : result) {
            PostDto postDto = addMyLike(postEntity, userEntity.getEmail());
            postDtoList.add(postDto);
        }
        log.info("IN getSomePost - {} posts found", posts.getTotalElements());
        return new BaseResponseSomeDto<>(postDtoList, offset, perPage, (int) total, "");
    }

    @Override
    public PostEntity findById(Long id) {
        PostEntity result = postRepository.findById(id).orElse(null);
        if (result == null) {
            log.info("IN findById - no post found by id: {}", id);
            return null;
        }
        log.info("IN findById - post: {} found by id", result);
        return result;
    }

    @Override
    public void delete(Long id) {
        postRepository.deleteById(id);
        log.info("IN delete - post with id: {} successfully deleted", id);
    }

    @Override
    public BaseResponseSomeDto<List<PostDto>> getUserPosts(Long userId, int offset, int itemPerPage, Principal principal) {
        UserEntity authorEntity = userService.findById(userId);
        UserEntity userEntity = userService.findByEmail(principal.getName());

        Pageable pageable = PageRequest.of(offset /= itemPerPage, itemPerPage);
        Page<PostEntity> userPosts;
        List<PostDto> userPostsDto = new ArrayList<>();
        if (authorEntity.equals(userEntity)) {
            userPosts = postRepository.getMyPosts(authorEntity, pageable);
        } else {
            userPosts = postRepository.getUserPosts(authorEntity, LocalDateTime.now(), pageable);
        }
        userPosts.forEach(post -> {
            PostDto postDto = addMyLike(post, principal.getName());
            userPostsDto.add(postDto);
        });
        log.info("In getUserPosts by user: {} get {} posts", authorEntity, userPosts.getTotalElements());
        return new BaseResponseSomeDto<>(userPostsDto, offset, itemPerPage, (int) userPosts.getTotalElements(), "");
    }

    @Override
    public BaseResponseDto<PostDto> createPost(CreatePostDto createPostDto, Long authorId, String publishDate) {
        UserEntity author = userService.findById(authorId);
        LocalDateTime publishDateTime = createPublishDate(publishDate);
        PostEntity postEntity = createPostDto.toUser();
        postEntity.setTime(publishDateTime);
        postEntity.setIsBlocked(false);
        postEntity.setLikes(0);
        postEntity.setAuthor(author);
        postRepository.save(postEntity);

        String text = postEntity.getPostText();
        List<String> urlList = new ArrayList<>();
        Pattern pattern = Pattern.compile("<img[^>]*src\\s*=\\s*\"([^\"]*)");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String txt = text.substring(matcher.start(0), matcher.end(0));
            urlList.add(txt.replaceAll("^.+/", "").replaceAll("\\..*", ""));
        }

        for (String str : urlList) {
            if (!str.isEmpty()) {
                FileEntity fileEntity = new FileEntity();
                fileEntity.setPath(str);
                fileEntity.setPostEntity(postEntity);
                fileRepository.save(fileEntity);
            }
        }

        createPostDto.getTags().forEach(tagName -> {
            TagEntity tag = tagEntityRepository.findByName(tagName);
            if (tag == null) {
                tag = new TagEntity();
                tag.setName(tagName);
                tagEntityRepository.save(tag);
            }
            Tag2PostEntity tag2Post = new Tag2PostEntity();
            tag2Post.setPost(postEntity);
            tag2Post.setTag(tag);
            tag2PostEntityRepository.save(tag2Post);
        });
        log.info("In createPost - post: {} successfully creating", postEntity);
        return new BaseResponseDto<>(PostDto.fromPost(postEntity, new BaseResponseSomeDto<>()), "");
    }

    @Override
    public BaseResponseDto<TagDto> createTag(TagDto tagDto) {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setName(tagDto.getTag());
        tagEntityRepository.save(tagEntity);
        tagDto.setId(tagEntity.getId());

        return new BaseResponseDto<>(tagDto, "");
    }

    @Override
    public BaseResponseSomeDto<List<TagDto>> getTags(String tag, int offset, int itemPerPage) {
        Pageable pageable = PageRequest.of(offset /= itemPerPage, itemPerPage);
        Page<TagEntity> page;
        if (tag == null) {
            return new BaseResponseSomeDto<>();
        } else {
            page = tagEntityRepository.findByName(tag, pageable);
        }
        long postCount = page.getTotalElements();
        List<TagDto> arrayList = new ArrayList<>();
        if (page.getSize() > 0) {
            page.forEach(tagEntity -> arrayList.add(new TagDto(tagEntity.getId(), tagEntity.getName())));
            return new BaseResponseSomeDto<>(arrayList, offset, itemPerPage, (int) postCount, "");
        } else {
            return new BaseResponseSomeDto<>();
        }
    }

    @Override
    public BaseResponseDto<AdditionalPropDto> deleteTag(Long id) {
        return new BaseResponseDto<>(new AdditionalPropDto("ok"), "");

    }

    @Override
    public BaseResponseDto<PostDto> updatePost(Long id, CreatePostDto createPostDto, String email) {
        UserEntity userEntity = userService.findByEmail(email);


        PostEntity postEntity = postRepository.getById(id);
        if (!postEntity.getAuthor().equals(userEntity)) {
            return new BaseResponseDto<>(null, "Чужой пост нельзя изменять");
        }
        if (postEntity.getIsDeleted() || postEntity.getIsBlocked()) {
            return new BaseResponseDto<>(null, "Пост был удалён или заблокирован. Изменение поста не доступно");
        }
        postEntity.setPostText(createPostDto.getPostText());
        postEntity.setTitle(createPostDto.getTitle());
        tag2PostEntityRepository.deleteTag2PostWithPostId(postEntity);
        postRepository.save(postEntity);

        createPostDto.getTags().forEach(tagName -> {
            TagEntity tag = tagEntityRepository.findByName(tagName);
            if (tag == null) {
                tag = new TagEntity();
                tag.setName(tagName);
                tagEntityRepository.save(tag);
            }
            Tag2PostEntity tag2Post = new Tag2PostEntity();
            tag2Post.setPost(postEntity);
            tag2Post.setTag(tag);
            tag2PostEntityRepository.save(tag2Post);
        });
        log.info("In updatePost - post: {} successfully updating", postEntity);
        return new BaseResponseDto<>(addMyLike(postEntity, email), "");
    }

    private LocalDateTime createPublishDate(String publishDate) {
        log.info("Дата публикации: " + publishDate);
        if (publishDate.equals("0")) {
            return LocalDateTime.now();
        } else {
            long publishLongTime = Long.parseLong(publishDate.substring(0, publishDate.length() - 3));
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(publishLongTime),
                    TimeZone.getDefault().toZoneId());
        }
    }

    public PostDto addMyLike(PostEntity post, String email) {
        PostDto postDto = PostDto.fromPost(post, commentsService.getPostComments(post.getId(), 0, 5, email));
        if (postDto.isDeleted()) {
            postDto.setType("DELETED");
        } else if (postDto.isBlocked()) {
            postDto.setType("BLOCKED");
        } else if (postDto.getTime().isAfter(LocalDateTime.now())) {
            postDto.setType("QUEUED");
        }
        LikeEntity likeEntity = likesRepository.getPostLikeByPerson(userService.findByEmail(email), post);
        if (likeEntity != null) {
            postDto.setMyLike(true);
        }
        return postDto;
    }

    @Override
    public BaseResponseDto<PostDto> getPostById(Long postId, String email) {
        PostDto postDto = addMyLike(postRepository.getById(postId), email);
        return new BaseResponseDto<>(postDto, "");
    }

    @Override
    public BaseResponseDto<PostDto> deletePost(Long postId, Principal principal) {
        UserEntity userEntity = userService.findByEmail(principal.getName());
        PostEntity postEntity = postRepository.getById(postId);
        if (!postEntity.getAuthor().equals(userEntity)) {
            return new BaseResponseDto<>(null, "Удалить пост может только автор поста");
        }
        postEntity.setIsDeleted(true);
        postRepository.save(postEntity);
        return getPostById(postId, principal.getName());
    }

    @Override
    public BaseResponseDto<PostDto> recoverPost(Long postId, Principal principal) {
        UserEntity userEntity = userService.findByEmail(principal.getName());
        if (!postRepository.getById(postId).getAuthor().equals(userEntity)) {
            return new BaseResponseDto<>(null, "Восстановить пост может только автор поста");
        }
        PostEntity postEntity = postRepository.getById(postId);
        postEntity.setIsDeleted(false);
        postRepository.save(postEntity);
        PostDto postDto = addMyLike(postEntity, principal.getName());
        return new BaseResponseDto<>(postDto, "");
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void finalRemovePost() {
        LocalDateTime deleteTime = LocalDateTime.now().minusDays(daysBeforeDeleted);
        List<PostEntity> posts = postRepository.getPostsForDelete(deleteTime);

        posts.forEach(post -> {
            List<CommentEntity> postComments = commentsRepository.findByPostEntityForDelete(post);
            postComments.forEach(commentsRepository::delete);
            log.info("In finalRemovePost in post {} {} comments was removed", post.getId(), postComments.size());

            postRepository.delete(post);
        });
        log.info("In finalRemovePost {} posts was removed", posts.size());

    }
}
