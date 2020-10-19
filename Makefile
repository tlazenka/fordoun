.PHONY: test
test:
	./gradlew --rerun-tasks allTest
        
.PHONY: test-docker
test-docker:
	docker-compose run --rm app ./gradlew --rerun-tasks allTest
        
.PHONY: format
format:
	docker-compose run --rm format
