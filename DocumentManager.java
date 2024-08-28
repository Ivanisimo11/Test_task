import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class DocumentManager {

    private final Map<String, Document> documentStorage = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(@NonNull Document document) {
        if(document.getId() ==null) {
            document.setId(UUID.randomUUID().toString());
            document.setCreated(Instant.now());
        }

        documentStorage.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return documentStorage.values().stream()
                .filter(doc -> matches(doc, request))
                .collect(Collectors.toList());
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(@NonNull String id) {
        return Optional.ofNullable(documentStorage.get(id));
    }

    private boolean matches(Document document,SearchRequest request) {
        if (request == null) {
            return true;
        }

        if (request.getTitlePrefixes() != null && request.getTitlePrefixes().stream()
                .noneMatch(prefix -> document.getTitle().startsWith(prefix))) {
            return false;
        }

        if (request.getContainsContents() != null && request.getContainsContents().stream()
                .noneMatch(content -> document.getContent().contains(content))) {
            return false;
        }

        if (request.getAuthorIds() != null && !request.getAuthorIds().contains(document.getAuthor().getId())) {
            return false;
        }

        if (request.getCreatedFrom() != null && document.getCreated().isBefore(request.getCreatedFrom())) {
            return false;
        }

        return request.getCreatedTo() == null || !document.getCreated().isAfter(request.getCreatedTo());
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;

        public SearchRequest(List<String> titlePrefixes, List<String> containsContents, List<String> authorIds, Instant createdFrom, Instant createdTo) {
            this.titlePrefixes = titlePrefixes;
            this.containsContents = containsContents;
            this.authorIds = authorIds;
            this.createdFrom = createdFrom;
            this.createdTo = createdTo;
        }

        public List<String> getTitlePrefixes() {
            return titlePrefixes;
        }

        public List<String> getContainsContents() {
            return containsContents;
        }

              public List<String> getAuthorIds() {
            return authorIds;
        }

        public Instant getCreatedFrom() {
            return createdFrom;
        }

        public Instant getCreatedTo() {
            return createdTo;
        }
    }

    @Data
    @Builder
    @RequiredArgsConstructor
    public static class Document {
        private String id;
        private @NonNull String title;
        private @NonNull String content;
        private @NonNull Author author;
        private Instant created;

        public Document(String id, @NonNull String title, @NonNull String content, @NonNull Author author, Instant created) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.author = author;
            this.created = created;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public Author getAuthor() {
            return author;
        }

        public Instant getCreated() {
            return created;
        }

        public void setCreated(Instant created) {
            this.created = created;
        }
    }

    @Data
    @Builder
    public static class Author {

        public Author(String id, String name) {
            this.id = id;
            this.name = name;
        }

        private String id;
        private String name;

        public String getId() {
            return id;
        }
    }
}
