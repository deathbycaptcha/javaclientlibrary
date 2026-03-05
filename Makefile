# DeathByCaptcha Unified Java SDK Makefile
# This Makefile provides an alternative to Maven for building the project
# For Maven users: use 'mvn clean install' instead

JAVAC = javac
JAR = jar
JAVADOC = javadoc

NAME = DeathByCaptcha
SRC_DIR = src/main/java
BUILD_DIR = target/classes
JAR_DIR = target
DOC_DIR = target/docs

OPT_SRC = $(SRC_DIR)/org/*/*.java
LIB_SRC = $(SRC_DIR)/com/$(NAME)/*.java
EXAMPLES_SRC = $(SRC_DIR)/examples/Example*.java

all: clean lib jar doc examples

clean:
	find $(SRC_DIR) -type f -name \*.class -delete 2>/dev/null || true
	rm -rf $(BUILD_DIR) $(JAR_DIR) $(DOC_DIR)
	mkdir -p $(BUILD_DIR) $(JAR_DIR) $(DOC_DIR)

.PHONY: lib
lib: clean
	$(JAVAC) -d $(BUILD_DIR) $(OPT_SRC) $(LIB_SRC)

.PHONY: jar
jar: lib
	mkdir -p $(JAR_DIR)
	$(JAR) cf $(JAR_DIR)/org.base64.jar -C $(BUILD_DIR) org/base64
	$(JAR) cf $(JAR_DIR)/org.json.jar -C $(BUILD_DIR) org/json
	$(JAR) cf $(JAR_DIR)/com.DeathByCaptcha.jar -C $(BUILD_DIR) com/DeathByCaptcha

.PHONY: doc
doc:
	$(JAVADOC) -public -d $(DOC_DIR) $(LIB_SRC)

.PHONY: examples
examples: lib
	$(JAVAC) -cp $(BUILD_DIR) -d $(BUILD_DIR) $(EXAMPLES_SRC)

.PHONY: package
package: all
	zip -9rX deathbycaptcha-java-sdk.zip src/ pom.xml Makefile README.md LICENSE .gitignore -x \*/.git/\* \*/target/\* \*/.class

help:
	@echo "DeathByCaptcha Java Unified SDK - Makefile"
	@echo ""
	@echo "Usage: make [target]"
	@echo ""
	@echo "Targets:"
	@echo "  all       - Build everything (default)"
	@echo "  clean     - Remove compiled files"
	@echo "  lib       - Compile core library classes"
	@echo "  jar       - Create JAR files"
	@echo "  doc       - Generate Javadoc"
	@echo "  examples  - Compile example files"
	@echo "  package   - Create distribution ZIP"
	@echo "  help      - Show this help message"
	@echo ""
	@echo "Note: For Maven users, use 'mvn clean install' instead"

