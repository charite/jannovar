.PHONY: default style

default: style test

test:
	mvn test

style:
	mvn com.coveo:fmt-maven-plugin:format -Dverbose=true
