begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|LifecycleScope
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
name|RandomizedContext
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
name|SysGlobals
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
name|annotations
operator|.
name|Listeners
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
name|annotations
operator|.
name|TestGroup
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
name|annotations
operator|.
name|ThreadLeakLingering
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
name|annotations
operator|.
name|ThreadLeakScope
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
name|annotations
operator|.
name|ThreadLeakScope
operator|.
name|Scope
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
name|annotations
operator|.
name|TimeoutSuite
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
name|generators
operator|.
name|RandomInts
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
name|generators
operator|.
name|RandomPicks
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
name|generators
operator|.
name|RandomStrings
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|QueryCache
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
name|uninverting
operator|.
name|UninvertingReader
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
name|LuceneTestCase
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
name|LuceneTestCase
operator|.
name|SuppressCodecs
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
name|TestUtil
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
name|TimeUnits
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
name|io
operator|.
name|PathUtils
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
name|ImmutableSettings
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
name|util
operator|.
name|concurrent
operator|.
name|EsExecutors
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
name|junit
operator|.
name|listeners
operator|.
name|LoggingListener
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
name|junit
operator|.
name|listeners
operator|.
name|ReproduceInfoPrinter
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
name|Assume
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
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Inherited
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Target
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_comment
comment|/**  * The new base test class, with all the goodies  */
end_comment

begin_class
annotation|@
name|Listeners
argument_list|(
block|{
name|ReproduceInfoPrinter
operator|.
name|class
block|,
name|LoggingListener
operator|.
name|class
block|}
argument_list|)
annotation|@
name|ThreadLeakScope
argument_list|(
name|Scope
operator|.
name|SUITE
argument_list|)
annotation|@
name|ThreadLeakLingering
argument_list|(
name|linger
operator|=
literal|5000
argument_list|)
comment|// 5 sec lingering
annotation|@
name|TimeoutSuite
argument_list|(
name|millis
operator|=
literal|20
operator|*
name|TimeUnits
operator|.
name|MINUTE
argument_list|)
annotation|@
name|LuceneTestCase
operator|.
name|SuppressSysoutChecks
argument_list|(
name|bugUrl
operator|=
literal|"we log a lot on purpose"
argument_list|)
annotation|@
name|Ignore
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"SimpleText"
block|,
literal|"Memory"
block|,
literal|"CheapBastard"
block|,
literal|"Direct"
block|}
argument_list|)
comment|// slow ones
annotation|@
name|LuceneTestCase
operator|.
name|SuppressReproduceLine
DECL|class|ESTestCase
specifier|public
specifier|abstract
class|class
name|ESTestCase
extends|extends
name|LuceneTestCase
block|{
static|static
block|{
name|SecurityHack
operator|.
name|ensureInitialized
argument_list|()
expr_stmt|;
block|}
comment|// setup mock filesystems for this test run. we change PathUtils
comment|// so that all accesses are plumbed thru any mock wrappers
annotation|@
name|BeforeClass
DECL|method|setUpFileSystem
specifier|public
specifier|static
name|void
name|setUpFileSystem
parameter_list|()
block|{
try|try
block|{
name|Field
name|field
init|=
name|PathUtils
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"DEFAULT"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|field
operator|.
name|set
argument_list|(
literal|null
argument_list|,
name|LuceneTestCase
operator|.
name|getBaseTempDirForTestClass
argument_list|()
operator|.
name|getFileSystem
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Before
DECL|method|disableQueryCache
specifier|public
name|void
name|disableQueryCache
parameter_list|()
block|{
comment|// TODO: Parent/child and other things does not work with the query cache
name|IndexSearcher
operator|.
name|setDefaultQueryCache
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|restoreFileSystem
specifier|public
specifier|static
name|void
name|restoreFileSystem
parameter_list|()
block|{
try|try
block|{
name|Field
name|field1
init|=
name|PathUtils
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"ACTUAL_DEFAULT"
argument_list|)
decl_stmt|;
name|field1
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Field
name|field2
init|=
name|PathUtils
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"DEFAULT"
argument_list|)
decl_stmt|;
name|field2
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|field2
operator|.
name|set
argument_list|(
literal|null
argument_list|,
name|field1
operator|.
name|get
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|After
DECL|method|ensureNoFieldCacheUse
specifier|public
name|void
name|ensureNoFieldCacheUse
parameter_list|()
block|{
comment|// field cache should NEVER get loaded.
name|String
index|[]
name|entries
init|=
name|UninvertingReader
operator|.
name|getUninvertedStats
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"fieldcache must never be used, got="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|entries
argument_list|)
argument_list|,
literal|0
argument_list|,
name|entries
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|// old shit:
comment|/**      * The number of concurrent JVMs used to run the tests, Default is<tt>1</tt>      */
DECL|field|CHILD_JVM_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|CHILD_JVM_COUNT
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
name|SysGlobals
operator|.
name|CHILDVM_SYSPROP_JVM_COUNT
argument_list|,
literal|"1"
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      * The child JVM ordinal of this JVM. Default is<tt>0</tt>      */
DECL|field|CHILD_JVM_ID
specifier|public
specifier|static
specifier|final
name|int
name|CHILD_JVM_ID
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
name|SysGlobals
operator|.
name|CHILDVM_SYSPROP_JVM_ID
argument_list|,
literal|"0"
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      * Annotation for backwards compat tests      */
annotation|@
name|Inherited
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|ElementType
operator|.
name|TYPE
argument_list|)
annotation|@
name|TestGroup
argument_list|(
name|enabled
operator|=
literal|false
argument_list|,
name|sysProperty
operator|=
name|TESTS_BACKWARDS_COMPATIBILITY
argument_list|)
DECL|interface|Backwards
specifier|public
annotation_defn|@interface
name|Backwards
block|{     }
comment|/**      * Key used to set the path for the elasticsearch executable used to run backwards compatibility tests from      * via the commandline -D{@value #TESTS_BACKWARDS_COMPATIBILITY}      */
DECL|field|TESTS_BACKWARDS_COMPATIBILITY
specifier|public
specifier|static
specifier|final
name|String
name|TESTS_BACKWARDS_COMPATIBILITY
init|=
literal|"tests.bwc"
decl_stmt|;
DECL|field|TESTS_BACKWARDS_COMPATIBILITY_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|TESTS_BACKWARDS_COMPATIBILITY_VERSION
init|=
literal|"tests.bwc.version"
decl_stmt|;
comment|/**      * Key used to set the path for the elasticsearch executable used to run backwards compatibility tests from      * via the commandline -D{@value #TESTS_BACKWARDS_COMPATIBILITY_PATH}      */
DECL|field|TESTS_BACKWARDS_COMPATIBILITY_PATH
specifier|public
specifier|static
specifier|final
name|String
name|TESTS_BACKWARDS_COMPATIBILITY_PATH
init|=
literal|"tests.bwc.path"
decl_stmt|;
comment|/**      * Annotation for REST tests      */
annotation|@
name|Inherited
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|ElementType
operator|.
name|TYPE
argument_list|)
annotation|@
name|TestGroup
argument_list|(
name|enabled
operator|=
literal|true
argument_list|,
name|sysProperty
operator|=
name|TESTS_REST
argument_list|)
DECL|interface|Rest
specifier|public
annotation_defn|@interface
name|Rest
block|{     }
comment|/**      * Property that allows to control whether the REST tests are run (default) or not      */
DECL|field|TESTS_REST
specifier|public
specifier|static
specifier|final
name|String
name|TESTS_REST
init|=
literal|"tests.rest"
decl_stmt|;
comment|/**      * Annotation for integration tests      */
annotation|@
name|Inherited
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|ElementType
operator|.
name|TYPE
argument_list|)
annotation|@
name|TestGroup
argument_list|(
name|enabled
operator|=
literal|true
argument_list|,
name|sysProperty
operator|=
name|SYSPROP_INTEGRATION
argument_list|)
DECL|interface|Integration
specifier|public
annotation_defn|@interface
name|Integration
block|{     }
comment|// --------------------------------------------------------------------
comment|// Test groups, system properties and other annotations modifying tests
comment|// --------------------------------------------------------------------
comment|/**      * @see #ignoreAfterMaxFailures      */
DECL|field|SYSPROP_MAXFAILURES
specifier|public
specifier|static
specifier|final
name|String
name|SYSPROP_MAXFAILURES
init|=
literal|"tests.maxfailures"
decl_stmt|;
comment|/**      * @see #ignoreAfterMaxFailures      */
DECL|field|SYSPROP_FAILFAST
specifier|public
specifier|static
specifier|final
name|String
name|SYSPROP_FAILFAST
init|=
literal|"tests.failfast"
decl_stmt|;
DECL|field|SYSPROP_INTEGRATION
specifier|public
specifier|static
specifier|final
name|String
name|SYSPROP_INTEGRATION
init|=
literal|"tests.integration"
decl_stmt|;
DECL|field|SYSPROP_PROCESSORS
specifier|public
specifier|static
specifier|final
name|String
name|SYSPROP_PROCESSORS
init|=
literal|"tests.processors"
decl_stmt|;
comment|// -----------------------------------------------------------------
comment|// Truly immutable fields and constants, initialized once and valid
comment|// for all suites ever since.
comment|// -----------------------------------------------------------------
DECL|field|TESTS_PROCESSORS
specifier|public
specifier|static
specifier|final
name|int
name|TESTS_PROCESSORS
decl_stmt|;
static|static
block|{
name|String
name|processors
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|SYSPROP_PROCESSORS
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|// mvn sets "" as default
if|if
condition|(
name|processors
operator|==
literal|null
operator|||
name|processors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|processors
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|EsExecutors
operator|.
name|boundedNumberOfProcessors
argument_list|(
name|ImmutableSettings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|TESTS_PROCESSORS
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|processors
argument_list|)
expr_stmt|;
block|}
comment|// -----------------------------------------------------------------
comment|// Suite and test case setup/ cleanup.
comment|// -----------------------------------------------------------------
comment|/** MockFSDirectoryService sets this: */
DECL|field|checkIndexFailed
specifier|public
specifier|static
name|boolean
name|checkIndexFailed
decl_stmt|;
comment|/**      * For subclasses to override. Overrides must call {@code super.setUp()}.      */
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|checkIndexFailed
operator|=
literal|false
expr_stmt|;
block|}
comment|/**      * For subclasses to override. Overrides must call {@code super.tearDown()}.      */
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
literal|"at least one shard failed CheckIndex"
argument_list|,
name|checkIndexFailed
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|// -----------------------------------------------------------------
comment|// Test facilities and facades for subclasses.
comment|// -----------------------------------------------------------------
comment|/**      * Registers a {@link Closeable} resource that should be closed after the test      * completes.      *      * @return<code>resource</code> (for call chaining).      */
annotation|@
name|Override
DECL|method|closeAfterTest
specifier|public
parameter_list|<
name|T
extends|extends
name|Closeable
parameter_list|>
name|T
name|closeAfterTest
parameter_list|(
name|T
name|resource
parameter_list|)
block|{
return|return
name|RandomizedContext
operator|.
name|current
argument_list|()
operator|.
name|closeAtEnd
argument_list|(
name|resource
argument_list|,
name|LifecycleScope
operator|.
name|TEST
argument_list|)
return|;
block|}
comment|/**      * Registers a {@link Closeable} resource that should be closed after the suite      * completes.      *      * @return<code>resource</code> (for call chaining).      */
DECL|method|closeAfterSuite
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|Closeable
parameter_list|>
name|T
name|closeAfterSuite
parameter_list|(
name|T
name|resource
parameter_list|)
block|{
return|return
name|RandomizedContext
operator|.
name|current
argument_list|()
operator|.
name|closeAtEnd
argument_list|(
name|resource
argument_list|,
name|LifecycleScope
operator|.
name|SUITE
argument_list|)
return|;
block|}
comment|// old helper stuff, a lot of it is bad news and we should see if its all used
comment|/**      * Returns a "scaled" random number between min and max (inclusive). The number of       * iterations will fall between [min, max], but the selection will also try to       * achieve the points below:       *<ul>      *<li>the multiplier can be used to move the number of iterations closer to min      *   (if it is smaller than 1) or closer to max (if it is larger than 1). Setting      *   the multiplier to 0 will always result in picking min.</li>      *<li>on normal runs, the number will be closer to min than to max.</li>      *<li>on nightly runs, the number will be closer to max than to min.</li>      *</ul>      *       * @see #multiplier()      *       * @param min Minimum (inclusive).      * @param max Maximum (inclusive).      * @return Returns a random number between min and max.      */
DECL|method|scaledRandomIntBetween
specifier|public
specifier|static
name|int
name|scaledRandomIntBetween
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
if|if
condition|(
name|min
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"min must be>= 0: "
operator|+
name|min
argument_list|)
throw|;
if|if
condition|(
name|min
operator|>
name|max
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"max must be>= min: "
operator|+
name|min
operator|+
literal|", "
operator|+
name|max
argument_list|)
throw|;
name|double
name|point
init|=
name|Math
operator|.
name|min
argument_list|(
literal|1
argument_list|,
name|Math
operator|.
name|abs
argument_list|(
name|random
argument_list|()
operator|.
name|nextGaussian
argument_list|()
argument_list|)
operator|*
literal|0.3
argument_list|)
operator|*
name|RANDOM_MULTIPLIER
decl_stmt|;
name|double
name|range
init|=
name|max
operator|-
name|min
decl_stmt|;
name|int
name|scaled
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|round
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|point
operator|*
name|range
argument_list|,
name|range
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|isNightly
argument_list|()
condition|)
block|{
return|return
name|max
operator|-
name|scaled
return|;
block|}
else|else
block|{
return|return
name|min
operator|+
name|scaled
return|;
block|}
block|}
comment|/**       * A random integer from<code>min</code> to<code>max</code> (inclusive).      *       * @see #scaledRandomIntBetween(int, int)      */
DECL|method|randomIntBetween
specifier|public
specifier|static
name|int
name|randomIntBetween
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
return|return
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
return|;
block|}
comment|/**      * Returns a "scaled" number of iterations for loops which can have a variable      * iteration count. This method is effectively       * an alias to {@link #scaledRandomIntBetween(int, int)}.      */
DECL|method|iterations
specifier|public
specifier|static
name|int
name|iterations
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
return|return
name|scaledRandomIntBetween
argument_list|(
name|min
argument_list|,
name|max
argument_list|)
return|;
block|}
comment|/**       * An alias for {@link #randomIntBetween(int, int)}.       *       * @see #scaledRandomIntBetween(int, int)      */
DECL|method|between
specifier|public
specifier|static
name|int
name|between
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
return|return
name|randomIntBetween
argument_list|(
name|min
argument_list|,
name|max
argument_list|)
return|;
block|}
comment|/**      * The exact opposite of {@link #rarely()}.      */
DECL|method|frequently
specifier|public
specifier|static
name|boolean
name|frequently
parameter_list|()
block|{
return|return
operator|!
name|rarely
argument_list|()
return|;
block|}
DECL|method|randomBoolean
specifier|public
specifier|static
name|boolean
name|randomBoolean
parameter_list|()
block|{
return|return
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
return|;
block|}
DECL|method|randomByte
specifier|public
specifier|static
name|byte
name|randomByte
parameter_list|()
block|{
return|return
operator|(
name|byte
operator|)
name|getRandom
argument_list|()
operator|.
name|nextInt
argument_list|()
return|;
block|}
DECL|method|randomShort
specifier|public
specifier|static
name|short
name|randomShort
parameter_list|()
block|{
return|return
operator|(
name|short
operator|)
name|getRandom
argument_list|()
operator|.
name|nextInt
argument_list|()
return|;
block|}
DECL|method|randomInt
specifier|public
specifier|static
name|int
name|randomInt
parameter_list|()
block|{
return|return
name|getRandom
argument_list|()
operator|.
name|nextInt
argument_list|()
return|;
block|}
DECL|method|randomFloat
specifier|public
specifier|static
name|float
name|randomFloat
parameter_list|()
block|{
return|return
name|getRandom
argument_list|()
operator|.
name|nextFloat
argument_list|()
return|;
block|}
DECL|method|randomDouble
specifier|public
specifier|static
name|double
name|randomDouble
parameter_list|()
block|{
return|return
name|getRandom
argument_list|()
operator|.
name|nextDouble
argument_list|()
return|;
block|}
DECL|method|randomLong
specifier|public
specifier|static
name|long
name|randomLong
parameter_list|()
block|{
return|return
name|getRandom
argument_list|()
operator|.
name|nextLong
argument_list|()
return|;
block|}
comment|/**      * Making {@link Assume#assumeNotNull(Object...)} directly available.      */
DECL|method|assumeNotNull
specifier|public
specifier|static
name|void
name|assumeNotNull
parameter_list|(
name|Object
modifier|...
name|objects
parameter_list|)
block|{
name|Assume
operator|.
name|assumeNotNull
argument_list|(
name|objects
argument_list|)
expr_stmt|;
block|}
comment|/**      * Pick a random object from the given array. The array must not be empty.      */
DECL|method|randomFrom
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|randomFrom
parameter_list|(
name|T
modifier|...
name|array
parameter_list|)
block|{
return|return
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|array
argument_list|)
return|;
block|}
comment|/**      * Pick a random object from the given list.      */
DECL|method|randomFrom
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|randomFrom
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|)
block|{
return|return
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|list
argument_list|)
return|;
block|}
comment|/**      * Shortcut for {@link RandomizedContext#getRandom()}. Even though this method      * is static, it returns per-thread {@link Random} instance, so no race conditions      * can occur.      *       *<p>It is recommended that specific methods are used to pick random values.      */
DECL|method|getRandom
specifier|public
specifier|static
name|Random
name|getRandom
parameter_list|()
block|{
return|return
name|random
argument_list|()
return|;
block|}
comment|/**       * A random integer from 0..max (inclusive).       */
DECL|method|randomInt
specifier|public
specifier|static
name|int
name|randomInt
parameter_list|(
name|int
name|max
parameter_list|)
block|{
return|return
name|RandomInts
operator|.
name|randomInt
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|max
argument_list|)
return|;
block|}
comment|/** @see StringGenerator#ofCodeUnitsLength(Random, int, int) */
DECL|method|randomAsciiOfLengthBetween
specifier|public
specifier|static
name|String
name|randomAsciiOfLengthBetween
parameter_list|(
name|int
name|minCodeUnits
parameter_list|,
name|int
name|maxCodeUnits
parameter_list|)
block|{
return|return
name|RandomStrings
operator|.
name|randomAsciiOfLengthBetween
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|minCodeUnits
argument_list|,
name|maxCodeUnits
argument_list|)
return|;
block|}
comment|/** @see StringGenerator#ofCodeUnitsLength(Random, int, int) */
DECL|method|randomAsciiOfLength
specifier|public
specifier|static
name|String
name|randomAsciiOfLength
parameter_list|(
name|int
name|codeUnits
parameter_list|)
block|{
return|return
name|RandomStrings
operator|.
name|randomAsciiOfLength
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|codeUnits
argument_list|)
return|;
block|}
comment|/** @see StringGenerator#ofCodeUnitsLength(Random, int, int) */
DECL|method|randomUnicodeOfLengthBetween
specifier|public
specifier|static
name|String
name|randomUnicodeOfLengthBetween
parameter_list|(
name|int
name|minCodeUnits
parameter_list|,
name|int
name|maxCodeUnits
parameter_list|)
block|{
return|return
name|RandomStrings
operator|.
name|randomUnicodeOfLengthBetween
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|minCodeUnits
argument_list|,
name|maxCodeUnits
argument_list|)
return|;
block|}
comment|/** @see StringGenerator#ofCodeUnitsLength(Random, int, int) */
DECL|method|randomUnicodeOfLength
specifier|public
specifier|static
name|String
name|randomUnicodeOfLength
parameter_list|(
name|int
name|codeUnits
parameter_list|)
block|{
return|return
name|RandomStrings
operator|.
name|randomUnicodeOfLength
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|codeUnits
argument_list|)
return|;
block|}
comment|/** @see StringGenerator#ofCodePointsLength(Random, int, int) */
DECL|method|randomUnicodeOfCodepointLengthBetween
specifier|public
specifier|static
name|String
name|randomUnicodeOfCodepointLengthBetween
parameter_list|(
name|int
name|minCodePoints
parameter_list|,
name|int
name|maxCodePoints
parameter_list|)
block|{
return|return
name|RandomStrings
operator|.
name|randomUnicodeOfCodepointLengthBetween
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|minCodePoints
argument_list|,
name|maxCodePoints
argument_list|)
return|;
block|}
comment|/** @see StringGenerator#ofCodePointsLength(Random, int, int) */
DECL|method|randomUnicodeOfCodepointLength
specifier|public
specifier|static
name|String
name|randomUnicodeOfCodepointLength
parameter_list|(
name|int
name|codePoints
parameter_list|)
block|{
return|return
name|RandomStrings
operator|.
name|randomUnicodeOfCodepointLength
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|codePoints
argument_list|)
return|;
block|}
comment|/** @see StringGenerator#ofCodeUnitsLength(Random, int, int) */
DECL|method|randomRealisticUnicodeOfLengthBetween
specifier|public
specifier|static
name|String
name|randomRealisticUnicodeOfLengthBetween
parameter_list|(
name|int
name|minCodeUnits
parameter_list|,
name|int
name|maxCodeUnits
parameter_list|)
block|{
return|return
name|RandomStrings
operator|.
name|randomRealisticUnicodeOfLengthBetween
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|minCodeUnits
argument_list|,
name|maxCodeUnits
argument_list|)
return|;
block|}
comment|/** @see StringGenerator#ofCodeUnitsLength(Random, int, int) */
DECL|method|randomRealisticUnicodeOfLength
specifier|public
specifier|static
name|String
name|randomRealisticUnicodeOfLength
parameter_list|(
name|int
name|codeUnits
parameter_list|)
block|{
return|return
name|RandomStrings
operator|.
name|randomRealisticUnicodeOfLength
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|codeUnits
argument_list|)
return|;
block|}
comment|/** @see StringGenerator#ofCodePointsLength(Random, int, int) */
DECL|method|randomRealisticUnicodeOfCodepointLengthBetween
specifier|public
specifier|static
name|String
name|randomRealisticUnicodeOfCodepointLengthBetween
parameter_list|(
name|int
name|minCodePoints
parameter_list|,
name|int
name|maxCodePoints
parameter_list|)
block|{
return|return
name|RandomStrings
operator|.
name|randomRealisticUnicodeOfCodepointLengthBetween
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|minCodePoints
argument_list|,
name|maxCodePoints
argument_list|)
return|;
block|}
comment|/** @see StringGenerator#ofCodePointsLength(Random, int, int) */
DECL|method|randomRealisticUnicodeOfCodepointLength
specifier|public
specifier|static
name|String
name|randomRealisticUnicodeOfCodepointLength
parameter_list|(
name|int
name|codePoints
parameter_list|)
block|{
return|return
name|RandomStrings
operator|.
name|randomRealisticUnicodeOfCodepointLength
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|codePoints
argument_list|)
return|;
block|}
comment|/**       * Return a random TimeZone from the available timezones on the system.      *       *<p>Warning: This test assumes the returned array of time zones is repeatable from jvm execution      * to jvm execution. It _may_ be different from jvm to jvm and as such, it can render      * tests execute in a different way.</p>      */
DECL|method|randomTimeZone
specifier|public
specifier|static
name|TimeZone
name|randomTimeZone
parameter_list|()
block|{
specifier|final
name|String
index|[]
name|availableIDs
init|=
name|TimeZone
operator|.
name|getAvailableIDs
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|availableIDs
argument_list|)
expr_stmt|;
return|return
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|randomFrom
argument_list|(
name|availableIDs
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Shortcut for {@link RandomizedContext#current()}.       */
DECL|method|getContext
specifier|public
specifier|static
name|RandomizedContext
name|getContext
parameter_list|()
block|{
return|return
name|RandomizedContext
operator|.
name|current
argument_list|()
return|;
block|}
comment|/**      * Returns true if we're running nightly tests.      * @see Nightly      */
DECL|method|isNightly
specifier|public
specifier|static
name|boolean
name|isNightly
parameter_list|()
block|{
return|return
name|getContext
argument_list|()
operator|.
name|isNightly
argument_list|()
return|;
block|}
comment|/**       * Returns a non-negative random value smaller or equal<code>max</code>. The value      * picked is affected by {@link #isNightly()} and {@link #multiplier()}.      *       *<p>This method is effectively an alias to:      *<pre>      * scaledRandomIntBetween(0, max)      *</pre>      *       * @see #scaledRandomIntBetween(int, int)      */
DECL|method|atMost
specifier|public
specifier|static
name|int
name|atMost
parameter_list|(
name|int
name|max
parameter_list|)
block|{
if|if
condition|(
name|max
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"atMost requires non-negative argument: "
operator|+
name|max
argument_list|)
throw|;
return|return
name|scaledRandomIntBetween
argument_list|(
literal|0
argument_list|,
name|max
argument_list|)
return|;
block|}
comment|/**      * Making {@link Assume#assumeTrue(boolean)} directly available.      */
DECL|method|assumeTrue
specifier|public
name|void
name|assumeTrue
parameter_list|(
name|boolean
name|condition
parameter_list|)
block|{
name|assumeTrue
argument_list|(
literal|"caller was too lazy to provide a reason"
argument_list|,
name|condition
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

