JAVA_HOME=$(shell unset JAVA_HOME; /usr/libexec/java_home -v 8)

.PHONY: verify
verify:
	mvn verify

.PHONY: install
install:
	mvn install

