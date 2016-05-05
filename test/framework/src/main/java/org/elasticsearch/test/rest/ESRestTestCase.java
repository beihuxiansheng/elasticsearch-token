begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|FileSystem
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|FileSystems
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardCopyOption
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|tasks
operator|.
name|list
operator|.
name|ListTasksAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Strings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|SuppressForbidden
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|client
operator|.
name|RestException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|client
operator|.
name|RestResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|parser
operator|.
name|RestTestParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|parser
operator|.
name|RestTestSuiteParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|section
operator|.
name|DoSection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|section
operator|.
name|ExecutableSection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|section
operator|.
name|RestTestSuite
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|section
operator|.
name|SkipSection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|section
operator|.
name|TestSection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|spec
operator|.
name|RestApi
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|spec
operator|.
name|RestSpec
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|support
operator|.
name|FileUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|sort
import|;
end_import

begin_comment
comment|/**  * Runs the clients test suite against an elasticsearch cluster.  */
end_comment

begin_class
DECL|class|ESRestTestCase
specifier|public
specifier|abstract
class|class
name|ESRestTestCase
extends|extends
name|ESTestCase
block|{
comment|/**      * Property that allows to control which REST tests get run. Supports comma separated list of tests      * or directories that contain tests e.g. -Dtests.rest.suite=index,get,create/10_with_id      */
DECL|field|REST_TESTS_SUITE
specifier|public
specifier|static
specifier|final
name|String
name|REST_TESTS_SUITE
init|=
literal|"tests.rest.suite"
decl_stmt|;
comment|/**      * Property that allows to blacklist some of the REST tests based on a comma separated list of globs      * e.g. -Dtests.rest.blacklist=get/10_basic/*      */
DECL|field|REST_TESTS_BLACKLIST
specifier|public
specifier|static
specifier|final
name|String
name|REST_TESTS_BLACKLIST
init|=
literal|"tests.rest.blacklist"
decl_stmt|;
comment|/**      * Property that allows to control whether spec validation is enabled or not (default true).      */
DECL|field|REST_TESTS_VALIDATE_SPEC
specifier|public
specifier|static
specifier|final
name|String
name|REST_TESTS_VALIDATE_SPEC
init|=
literal|"tests.rest.validate_spec"
decl_stmt|;
comment|/**      * Property that allows to control where the REST spec files need to be loaded from      */
DECL|field|REST_TESTS_SPEC
specifier|public
specifier|static
specifier|final
name|String
name|REST_TESTS_SPEC
init|=
literal|"tests.rest.spec"
decl_stmt|;
DECL|field|REST_LOAD_PACKAGED_TESTS
specifier|public
specifier|static
specifier|final
name|String
name|REST_LOAD_PACKAGED_TESTS
init|=
literal|"tests.rest.load_packaged"
decl_stmt|;
DECL|field|DEFAULT_TESTS_PATH
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_TESTS_PATH
init|=
literal|"/rest-api-spec/test"
decl_stmt|;
DECL|field|DEFAULT_SPEC_PATH
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_SPEC_PATH
init|=
literal|"/rest-api-spec/api"
decl_stmt|;
comment|/**      * This separator pattern matches ',' except it is preceded by a '\'. This allows us to support ',' within paths when it is escaped with      * a slash.      *      * For example, the path string "/a/b/c\,d/e/f,/foo/bar,/baz" is separated to "/a/b/c\,d/e/f", "/foo/bar" and "/baz".      *      * For reference, this regular expression feature is known as zero-width negative look-behind.      *      */
DECL|field|PATHS_SEPARATOR
specifier|private
specifier|static
specifier|final
name|String
name|PATHS_SEPARATOR
init|=
literal|"(?<!\\\\),"
decl_stmt|;
DECL|field|blacklistPathMatchers
specifier|private
specifier|final
name|List
argument_list|<
name|BlacklistedPathPatternMatcher
argument_list|>
name|blacklistPathMatchers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|clusterUrls
specifier|private
specifier|final
name|URL
index|[]
name|clusterUrls
decl_stmt|;
DECL|field|restTestExecutionContext
specifier|private
specifier|static
name|RestTestExecutionContext
name|restTestExecutionContext
decl_stmt|;
DECL|field|adminExecutionContext
specifier|private
specifier|static
name|RestTestExecutionContext
name|adminExecutionContext
decl_stmt|;
DECL|field|testCandidate
specifier|private
specifier|final
name|RestTestCandidate
name|testCandidate
decl_stmt|;
DECL|method|ESRestTestCase
specifier|public
name|ESRestTestCase
parameter_list|(
name|RestTestCandidate
name|testCandidate
parameter_list|)
block|{
name|this
operator|.
name|testCandidate
operator|=
name|testCandidate
expr_stmt|;
name|String
index|[]
name|blacklist
init|=
name|resolvePathsProperty
argument_list|(
name|REST_TESTS_BLACKLIST
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|entry
range|:
name|blacklist
control|)
block|{
name|this
operator|.
name|blacklistPathMatchers
operator|.
name|add
argument_list|(
operator|new
name|BlacklistedPathPatternMatcher
argument_list|(
name|entry
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|cluster
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.rest.cluster"
argument_list|)
decl_stmt|;
if|if
condition|(
name|cluster
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Must specify tests.rest.cluster for rest tests"
argument_list|)
throw|;
block|}
name|String
index|[]
name|stringUrls
init|=
name|cluster
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|clusterUrls
operator|=
operator|new
name|URL
index|[
name|stringUrls
operator|.
name|length
index|]
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
try|try
block|{
for|for
control|(
name|String
name|stringUrl
range|:
name|stringUrls
control|)
block|{
name|clusterUrls
index|[
name|i
operator|++
index|]
operator|=
operator|new
name|URL
argument_list|(
literal|"http://"
operator|+
name|stringUrl
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to parse cluster addresses for rest test"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|afterIfFailed
specifier|protected
name|void
name|afterIfFailed
parameter_list|(
name|List
argument_list|<
name|Throwable
argument_list|>
name|errors
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Stash dump on failure [{}]"
argument_list|,
name|XContentHelper
operator|.
name|toString
argument_list|(
name|restTestExecutionContext
operator|.
name|stash
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|afterIfFailed
argument_list|(
name|errors
argument_list|)
expr_stmt|;
block|}
DECL|method|createParameters
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Object
index|[]
argument_list|>
name|createParameters
parameter_list|(
name|int
name|id
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
throws|,
name|RestTestParseException
block|{
comment|//parse tests only if rest test group is enabled, otherwise rest tests might not even be available on file system
name|List
argument_list|<
name|RestTestCandidate
argument_list|>
name|restTestCandidates
init|=
name|collectTestCandidates
argument_list|(
name|id
argument_list|,
name|count
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|objects
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RestTestCandidate
name|restTestCandidate
range|:
name|restTestCandidates
control|)
block|{
name|objects
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|restTestCandidate
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|objects
return|;
block|}
DECL|method|collectTestCandidates
specifier|private
specifier|static
name|List
argument_list|<
name|RestTestCandidate
argument_list|>
name|collectTestCandidates
parameter_list|(
name|int
name|id
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|RestTestParseException
throws|,
name|IOException
block|{
name|List
argument_list|<
name|RestTestCandidate
argument_list|>
name|testCandidates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|FileSystem
name|fileSystem
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// don't make a try-with, getFileSystem returns null
comment|// ... and you can't close() the default filesystem
try|try
block|{
name|String
index|[]
name|paths
init|=
name|resolvePathsProperty
argument_list|(
name|REST_TESTS_SUITE
argument_list|,
name|DEFAULT_TESTS_PATH
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Path
argument_list|>
argument_list|>
name|yamlSuites
init|=
name|FileUtils
operator|.
name|findYamlSuites
argument_list|(
name|fileSystem
argument_list|,
name|DEFAULT_TESTS_PATH
argument_list|,
name|paths
argument_list|)
decl_stmt|;
name|RestTestSuiteParser
name|restTestSuiteParser
init|=
operator|new
name|RestTestSuiteParser
argument_list|()
decl_stmt|;
comment|//yaml suites are grouped by directory (effectively by api)
for|for
control|(
name|String
name|api
range|:
name|yamlSuites
operator|.
name|keySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|Path
argument_list|>
name|yamlFiles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|yamlSuites
operator|.
name|get
argument_list|(
name|api
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Path
name|yamlFile
range|:
name|yamlFiles
control|)
block|{
name|String
name|key
init|=
name|api
operator|+
name|yamlFile
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|mustExecute
argument_list|(
name|key
argument_list|,
name|id
argument_list|,
name|count
argument_list|)
condition|)
block|{
name|RestTestSuite
name|restTestSuite
init|=
name|restTestSuiteParser
operator|.
name|parse
argument_list|(
name|api
argument_list|,
name|yamlFile
argument_list|)
decl_stmt|;
for|for
control|(
name|TestSection
name|testSection
range|:
name|restTestSuite
operator|.
name|getTestSections
argument_list|()
control|)
block|{
name|testCandidates
operator|.
name|add
argument_list|(
operator|new
name|RestTestCandidate
argument_list|(
name|restTestSuite
argument_list|,
name|testSection
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|fileSystem
argument_list|)
expr_stmt|;
block|}
comment|//sort the candidates so they will always be in the same order before being shuffled, for repeatability
name|Collections
operator|.
name|sort
argument_list|(
name|testCandidates
argument_list|,
operator|new
name|Comparator
argument_list|<
name|RestTestCandidate
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|RestTestCandidate
name|o1
parameter_list|,
name|RestTestCandidate
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getTestPath
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getTestPath
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|testCandidates
return|;
block|}
DECL|method|mustExecute
specifier|private
specifier|static
name|boolean
name|mustExecute
parameter_list|(
name|String
name|test
parameter_list|,
name|int
name|id
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|int
name|hash
init|=
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
operator|(
name|long
operator|)
name|test
operator|.
name|hashCode
argument_list|()
argument_list|)
operator|%
name|count
argument_list|)
decl_stmt|;
return|return
name|hash
operator|==
name|id
return|;
block|}
DECL|method|resolvePathsProperty
specifier|private
specifier|static
name|String
index|[]
name|resolvePathsProperty
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|String
name|property
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|hasLength
argument_list|(
name|property
argument_list|)
condition|)
block|{
return|return
name|defaultValue
operator|==
literal|null
condition|?
name|Strings
operator|.
name|EMPTY_ARRAY
else|:
operator|new
name|String
index|[]
block|{
name|defaultValue
block|}
return|;
block|}
else|else
block|{
return|return
name|property
operator|.
name|split
argument_list|(
name|PATHS_SEPARATOR
argument_list|)
return|;
block|}
block|}
comment|/**      * Returns a new FileSystem to read REST resources, or null if they      * are available from classpath.      */
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"proper use of URL, hack around a JDK bug"
argument_list|)
DECL|method|getFileSystem
specifier|static
name|FileSystem
name|getFileSystem
parameter_list|()
throws|throws
name|IOException
block|{
comment|// REST suite handling is currently complicated, with lots of filtering and so on
comment|// For now, to work embedded in a jar, return a ZipFileSystem over the jar contents.
name|URL
name|codeLocation
init|=
name|FileUtils
operator|.
name|class
operator|.
name|getProtectionDomain
argument_list|()
operator|.
name|getCodeSource
argument_list|()
operator|.
name|getLocation
argument_list|()
decl_stmt|;
name|boolean
name|loadPackaged
init|=
name|RandomizedTest
operator|.
name|systemPropertyAsBoolean
argument_list|(
name|REST_LOAD_PACKAGED_TESTS
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|codeLocation
operator|.
name|getFile
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
operator|&&
name|loadPackaged
condition|)
block|{
try|try
block|{
comment|// hack around a bug in the zipfilesystem implementation before java 9,
comment|// its checkWritable was incorrect and it won't work without write permissions.
comment|// if we add the permission, it will open jars r/w, which is too scary! so copy to a safe r-w location.
name|Path
name|tmp
init|=
name|Files
operator|.
name|createTempFile
argument_list|(
literal|null
argument_list|,
literal|".jar"
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|in
init|=
name|codeLocation
operator|.
name|openStream
argument_list|()
init|)
block|{
name|Files
operator|.
name|copy
argument_list|(
name|in
argument_list|,
name|tmp
argument_list|,
name|StandardCopyOption
operator|.
name|REPLACE_EXISTING
argument_list|)
expr_stmt|;
block|}
return|return
name|FileSystems
operator|.
name|newFileSystem
argument_list|(
operator|new
name|URI
argument_list|(
literal|"jar:"
operator|+
name|tmp
operator|.
name|toUri
argument_list|()
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|emptyMap
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"couldn't open zipfilesystem: "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|BeforeClass
DECL|method|initExecutionContext
specifier|public
specifier|static
name|void
name|initExecutionContext
parameter_list|()
throws|throws
name|IOException
throws|,
name|RestException
block|{
name|String
index|[]
name|specPaths
init|=
name|resolvePathsProperty
argument_list|(
name|REST_TESTS_SPEC
argument_list|,
name|DEFAULT_SPEC_PATH
argument_list|)
decl_stmt|;
name|RestSpec
name|restSpec
init|=
literal|null
decl_stmt|;
name|FileSystem
name|fileSystem
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// don't make a try-with, getFileSystem returns null
comment|// ... and you can't close() the default filesystem
try|try
block|{
name|restSpec
operator|=
name|RestSpec
operator|.
name|parseFrom
argument_list|(
name|fileSystem
argument_list|,
name|DEFAULT_SPEC_PATH
argument_list|,
name|specPaths
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|fileSystem
argument_list|)
expr_stmt|;
block|}
name|validateSpec
argument_list|(
name|restSpec
argument_list|)
expr_stmt|;
name|restTestExecutionContext
operator|=
operator|new
name|RestTestExecutionContext
argument_list|(
name|restSpec
argument_list|)
expr_stmt|;
name|adminExecutionContext
operator|=
operator|new
name|RestTestExecutionContext
argument_list|(
name|restSpec
argument_list|)
expr_stmt|;
block|}
DECL|method|getAdminExecutionContext
specifier|protected
name|RestTestExecutionContext
name|getAdminExecutionContext
parameter_list|()
block|{
return|return
name|adminExecutionContext
return|;
block|}
DECL|method|validateSpec
specifier|private
specifier|static
name|void
name|validateSpec
parameter_list|(
name|RestSpec
name|restSpec
parameter_list|)
block|{
name|boolean
name|validateSpec
init|=
name|RandomizedTest
operator|.
name|systemPropertyAsBoolean
argument_list|(
name|REST_TESTS_VALIDATE_SPEC
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|validateSpec
condition|)
block|{
name|StringBuilder
name|errorMessage
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|RestApi
name|restApi
range|:
name|restSpec
operator|.
name|getApis
argument_list|()
control|)
block|{
if|if
condition|(
name|restApi
operator|.
name|getMethods
argument_list|()
operator|.
name|contains
argument_list|(
literal|"GET"
argument_list|)
operator|&&
name|restApi
operator|.
name|isBodySupported
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|restApi
operator|.
name|getMethods
argument_list|()
operator|.
name|contains
argument_list|(
literal|"POST"
argument_list|)
condition|)
block|{
name|errorMessage
operator|.
name|append
argument_list|(
literal|"\n- "
argument_list|)
operator|.
name|append
argument_list|(
name|restApi
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" supports GET with a body but doesn't support POST"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|errorMessage
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|errorMessage
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|After
DECL|method|wipeCluster
specifier|public
name|void
name|wipeCluster
parameter_list|()
throws|throws
name|Exception
block|{
comment|// wipe indices
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|deleteIndicesArgs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|deleteIndicesArgs
operator|.
name|put
argument_list|(
literal|"index"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
try|try
block|{
name|adminExecutionContext
operator|.
name|callApi
argument_list|(
literal|"indices.delete"
argument_list|,
name|deleteIndicesArgs
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RestException
name|e
parameter_list|)
block|{
comment|// 404 here just means we had no indexes
if|if
condition|(
name|e
operator|.
name|statusCode
argument_list|()
operator|!=
literal|404
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
comment|// wipe index templates
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|deleteTemplatesArgs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|deleteTemplatesArgs
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|adminExecutionContext
operator|.
name|callApi
argument_list|(
literal|"indices.delete_template"
argument_list|,
name|deleteTemplatesArgs
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
comment|// wipe snapshots
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|deleteSnapshotsArgs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|deleteSnapshotsArgs
operator|.
name|put
argument_list|(
literal|"repository"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|adminExecutionContext
operator|.
name|callApi
argument_list|(
literal|"snapshot.delete_repository"
argument_list|,
name|deleteSnapshotsArgs
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Logs a message if there are still running tasks. The reasoning is that any tasks still running are state the is trying to bleed into      * other tests.      */
annotation|@
name|After
DECL|method|logIfThereAreRunningTasks
specifier|public
name|void
name|logIfThereAreRunningTasks
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
throws|,
name|RestException
block|{
name|RestResponse
name|tasks
init|=
name|adminExecutionContext
operator|.
name|callApi
argument_list|(
literal|"tasks.list"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|emptyList
argument_list|()
argument_list|,
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|runningTasks
init|=
name|runningTasks
argument_list|(
name|tasks
argument_list|)
decl_stmt|;
comment|// Ignore the task list API - it doens't count against us
name|runningTasks
operator|.
name|remove
argument_list|(
name|ListTasksAction
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|runningTasks
operator|.
name|remove
argument_list|(
name|ListTasksAction
operator|.
name|NAME
operator|+
literal|"[n]"
argument_list|)
expr_stmt|;
if|if
condition|(
name|runningTasks
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|stillRunning
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|runningTasks
argument_list|)
decl_stmt|;
name|sort
argument_list|(
name|stillRunning
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"There are still tasks running after this test that might break subsequent tests {}."
argument_list|,
name|stillRunning
argument_list|)
expr_stmt|;
comment|/*          * This isn't a higher level log or outright failure because some of these tasks are run by the cluster in the background. If we          * could determine that some tasks are run by the user we'd fail the tests if those tasks were running and ignore any background          * tasks.          */
block|}
annotation|@
name|AfterClass
DECL|method|close
specifier|public
specifier|static
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|restTestExecutionContext
operator|!=
literal|null
condition|)
block|{
name|restTestExecutionContext
operator|.
name|close
argument_list|()
expr_stmt|;
name|adminExecutionContext
operator|.
name|close
argument_list|()
expr_stmt|;
name|restTestExecutionContext
operator|=
literal|null
expr_stmt|;
name|adminExecutionContext
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**      * Used to obtain settings for the REST client that is used to send REST requests.      */
DECL|method|restClientSettings
specifier|protected
name|Settings
name|restClientSettings
parameter_list|()
block|{
return|return
name|Settings
operator|.
name|EMPTY
return|;
block|}
comment|/** Returns the REST client settings used for admin actions like cleaning up after the test has completed. */
DECL|method|restAdminSettings
specifier|protected
name|Settings
name|restAdminSettings
parameter_list|()
block|{
return|return
name|restClientSettings
argument_list|()
return|;
comment|// default to the same client settings
block|}
comment|/** Returns the addresses the client uses to connect to the test cluster. */
DECL|method|getClusterUrls
specifier|protected
name|URL
index|[]
name|getClusterUrls
parameter_list|()
block|{
return|return
name|clusterUrls
return|;
block|}
annotation|@
name|Before
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
throws|,
name|RestException
block|{
comment|// admin context must be available for @After always, regardless of whether the test was blacklisted
name|adminExecutionContext
operator|.
name|initClient
argument_list|(
name|clusterUrls
argument_list|,
name|restAdminSettings
argument_list|()
argument_list|)
expr_stmt|;
name|adminExecutionContext
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|//skip test if it matches one of the blacklist globs
for|for
control|(
name|BlacklistedPathPatternMatcher
name|blacklistedPathMatcher
range|:
name|blacklistPathMatchers
control|)
block|{
name|String
name|testPath
init|=
name|testCandidate
operator|.
name|getSuitePath
argument_list|()
operator|+
literal|"/"
operator|+
name|testCandidate
operator|.
name|getTestSection
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|assumeFalse
argument_list|(
literal|"["
operator|+
name|testCandidate
operator|.
name|getTestPath
argument_list|()
operator|+
literal|"] skipped, reason: blacklisted"
argument_list|,
name|blacklistedPathMatcher
operator|.
name|isSuffixMatch
argument_list|(
name|testPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//The client needs non static info to get initialized, therefore it can't be initialized in the before class
name|restTestExecutionContext
operator|.
name|initClient
argument_list|(
name|clusterUrls
argument_list|,
name|restClientSettings
argument_list|()
argument_list|)
expr_stmt|;
name|restTestExecutionContext
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|//skip test if the whole suite (yaml file) is disabled
name|assumeFalse
argument_list|(
name|buildSkipMessage
argument_list|(
name|testCandidate
operator|.
name|getSuitePath
argument_list|()
argument_list|,
name|testCandidate
operator|.
name|getSetupSection
argument_list|()
operator|.
name|getSkipSection
argument_list|()
argument_list|)
argument_list|,
name|testCandidate
operator|.
name|getSetupSection
argument_list|()
operator|.
name|getSkipSection
argument_list|()
operator|.
name|skip
argument_list|(
name|restTestExecutionContext
operator|.
name|esVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//skip test if test section is disabled
name|assumeFalse
argument_list|(
name|buildSkipMessage
argument_list|(
name|testCandidate
operator|.
name|getTestPath
argument_list|()
argument_list|,
name|testCandidate
operator|.
name|getTestSection
argument_list|()
operator|.
name|getSkipSection
argument_list|()
argument_list|)
argument_list|,
name|testCandidate
operator|.
name|getTestSection
argument_list|()
operator|.
name|getSkipSection
argument_list|()
operator|.
name|skip
argument_list|(
name|restTestExecutionContext
operator|.
name|esVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|buildSkipMessage
specifier|private
specifier|static
name|String
name|buildSkipMessage
parameter_list|(
name|String
name|description
parameter_list|,
name|SkipSection
name|skipSection
parameter_list|)
block|{
name|StringBuilder
name|messageBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|skipSection
operator|.
name|isVersionCheck
argument_list|()
condition|)
block|{
name|messageBuilder
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
operator|.
name|append
argument_list|(
name|description
argument_list|)
operator|.
name|append
argument_list|(
literal|"] skipped, reason: ["
argument_list|)
operator|.
name|append
argument_list|(
name|skipSection
operator|.
name|getReason
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"] "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|messageBuilder
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
operator|.
name|append
argument_list|(
name|description
argument_list|)
operator|.
name|append
argument_list|(
literal|"] skipped, reason: features "
argument_list|)
operator|.
name|append
argument_list|(
name|skipSection
operator|.
name|getFeatures
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" not supported"
argument_list|)
expr_stmt|;
block|}
return|return
name|messageBuilder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
comment|//let's check that there is something to run, otherwise there might be a problem with the test section
if|if
condition|(
name|testCandidate
operator|.
name|getTestSection
argument_list|()
operator|.
name|getExecutableSections
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No executable sections loaded for ["
operator|+
name|testCandidate
operator|.
name|getTestPath
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|testCandidate
operator|.
name|getSetupSection
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"start setup test [{}]"
argument_list|,
name|testCandidate
operator|.
name|getTestPath
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|DoSection
name|doSection
range|:
name|testCandidate
operator|.
name|getSetupSection
argument_list|()
operator|.
name|getDoSections
argument_list|()
control|)
block|{
name|doSection
operator|.
name|execute
argument_list|(
name|restTestExecutionContext
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"end setup test [{}]"
argument_list|,
name|testCandidate
operator|.
name|getTestPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|restTestExecutionContext
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|ExecutableSection
name|executableSection
range|:
name|testCandidate
operator|.
name|getTestSection
argument_list|()
operator|.
name|getExecutableSections
argument_list|()
control|)
block|{
name|executableSection
operator|.
name|execute
argument_list|(
name|restTestExecutionContext
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|runningTasks
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|runningTasks
parameter_list|(
name|RestResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|runningTasks
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nodes
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|response
operator|.
name|evaluate
argument_list|(
literal|"nodes"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|node
range|:
name|nodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nodeInfo
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|node
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nodeTasks
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|nodeInfo
operator|.
name|get
argument_list|(
literal|"tasks"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|taskAndName
range|:
name|nodeTasks
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|task
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|taskAndName
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|runningTasks
operator|.
name|add
argument_list|(
name|task
operator|.
name|get
argument_list|(
literal|"action"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|runningTasks
return|;
block|}
block|}
end_class

end_unit

