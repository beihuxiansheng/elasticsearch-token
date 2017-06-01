begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.upgrades
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|upgrades
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|ContentType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|StringEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|util
operator|.
name|EntityUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Response
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
name|Booleans
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
name|CheckedFunction
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
name|XContentBuilder
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
name|common
operator|.
name|xcontent
operator|.
name|json
operator|.
name|JsonXContent
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
name|support
operator|.
name|XContentMapValues
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
name|ESRestTestCase
import|;
end_import

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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|singletonMap
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
operator|.
name|jsonBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
import|;
end_import

begin_comment
comment|/**  * Tests to run before and after a full cluster restart. This is run twice,  * one with {@code tests.is_old_cluster} set to {@code true} against a cluster  * of an older version. The cluster is shutdown and a cluster of the new  * version is started with the same data directories and then this is rerun  * with {@code tests.is_old_cluster} set to {@code false}.  */
end_comment

begin_class
DECL|class|FullClusterRestartIT
specifier|public
class|class
name|FullClusterRestartIT
extends|extends
name|ESRestTestCase
block|{
DECL|field|REPO
specifier|private
specifier|static
specifier|final
name|String
name|REPO
init|=
literal|"/_snapshot/repo"
decl_stmt|;
DECL|field|runningAgainstOldCluster
specifier|private
specifier|final
name|boolean
name|runningAgainstOldCluster
init|=
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.is_old_cluster"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|oldClusterVersion
specifier|private
specifier|final
name|Version
name|oldClusterVersion
init|=
name|Version
operator|.
name|fromString
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.old_cluster_version"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|supportsLenientBooleans
specifier|private
specifier|final
name|boolean
name|supportsLenientBooleans
init|=
name|oldClusterVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_6_0_0_alpha1
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|preserveIndicesUponCompletion
specifier|protected
name|boolean
name|preserveIndicesUponCompletion
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|preserveReposUponCompletion
specifier|protected
name|boolean
name|preserveReposUponCompletion
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|testSearch
specifier|public
name|void
name|testSearch
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|index
init|=
name|getTestName
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
if|if
condition|(
name|runningAgainstOldCluster
condition|)
block|{
name|XContentBuilder
name|mappingsAndSettings
init|=
name|jsonBuilder
argument_list|()
decl_stmt|;
name|mappingsAndSettings
operator|.
name|startObject
argument_list|()
expr_stmt|;
block|{
name|mappingsAndSettings
operator|.
name|startObject
argument_list|(
literal|"settings"
argument_list|)
expr_stmt|;
name|mappingsAndSettings
operator|.
name|field
argument_list|(
literal|"number_of_shards"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|mappingsAndSettings
operator|.
name|field
argument_list|(
literal|"number_of_replicas"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|mappingsAndSettings
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|{
name|mappingsAndSettings
operator|.
name|startObject
argument_list|(
literal|"mappings"
argument_list|)
expr_stmt|;
name|mappingsAndSettings
operator|.
name|startObject
argument_list|(
literal|"doc"
argument_list|)
expr_stmt|;
name|mappingsAndSettings
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
expr_stmt|;
block|{
name|mappingsAndSettings
operator|.
name|startObject
argument_list|(
literal|"string"
argument_list|)
expr_stmt|;
name|mappingsAndSettings
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|mappingsAndSettings
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|{
name|mappingsAndSettings
operator|.
name|startObject
argument_list|(
literal|"dots_in_field_names"
argument_list|)
expr_stmt|;
name|mappingsAndSettings
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|mappingsAndSettings
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|mappingsAndSettings
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|mappingsAndSettings
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|mappingsAndSettings
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|mappingsAndSettings
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"PUT"
argument_list|,
literal|"/"
operator|+
name|index
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
operator|new
name|StringEntity
argument_list|(
name|mappingsAndSettings
operator|.
name|string
argument_list|()
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|numDocs
init|=
name|randomIntBetween
argument_list|(
literal|2000
argument_list|,
literal|3000
argument_list|)
decl_stmt|;
name|indexRandomDocuments
argument_list|(
name|index
argument_list|,
name|numDocs
argument_list|,
literal|true
argument_list|,
name|i
lambda|->
block|{
return|return
name|JsonXContent
operator|.
name|contentBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"string"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|)
operator|.
name|field
argument_list|(
literal|"int"
argument_list|,
name|randomInt
argument_list|(
literal|100
argument_list|)
argument_list|)
operator|.
name|field
argument_list|(
literal|"float"
argument_list|,
name|randomFloat
argument_list|()
argument_list|)
comment|// be sure to create a "proper" boolean (True, False) for the first document so that automapping is correct
operator|.
name|field
argument_list|(
literal|"bool"
argument_list|,
name|i
operator|>
literal|0
operator|&&
name|supportsLenientBooleans
condition|?
name|randomLenientBoolean
argument_list|()
else|:
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
literal|"field.with.dots"
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|)
comment|// TODO a binary field
operator|.
name|endObject
argument_list|()
return|;
block|}
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Refreshing [{}]"
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"POST"
argument_list|,
literal|"/"
operator|+
name|index
operator|+
literal|"/_refresh"
argument_list|)
expr_stmt|;
block|}
name|assertBasicSearchWorks
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
DECL|method|assertBasicSearchWorks
name|void
name|assertBasicSearchWorks
parameter_list|(
name|String
name|index
parameter_list|)
throws|throws
name|IOException
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> testing basic search"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|response
init|=
name|toMap
argument_list|(
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"GET"
argument_list|,
literal|"/"
operator|+
name|index
operator|+
literal|"/_search"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNoFailures
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|int
name|numDocs1
init|=
operator|(
name|int
operator|)
name|XContentMapValues
operator|.
name|extractValue
argument_list|(
literal|"hits.total"
argument_list|,
name|response
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Found {} in old index"
argument_list|,
name|numDocs1
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> testing basic search with sort"
argument_list|)
expr_stmt|;
name|String
name|searchRequestBody
init|=
literal|"{ \"sort\": [{ \"int\" : \"asc\" }]}"
decl_stmt|;
name|response
operator|=
name|toMap
argument_list|(
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"GET"
argument_list|,
literal|"/"
operator|+
name|index
operator|+
literal|"/_search"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
operator|new
name|StringEntity
argument_list|(
name|searchRequestBody
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoFailures
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|int
name|numDocs2
init|=
operator|(
name|int
operator|)
name|XContentMapValues
operator|.
name|extractValue
argument_list|(
literal|"hits.total"
argument_list|,
name|response
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numDocs1
argument_list|,
name|numDocs2
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> testing exists filter"
argument_list|)
expr_stmt|;
name|searchRequestBody
operator|=
literal|"{ \"query\": { \"exists\" : {\"field\": \"string\"} }}"
expr_stmt|;
name|response
operator|=
name|toMap
argument_list|(
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"GET"
argument_list|,
literal|"/"
operator|+
name|index
operator|+
literal|"/_search"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
operator|new
name|StringEntity
argument_list|(
name|searchRequestBody
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoFailures
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|numDocs2
operator|=
operator|(
name|int
operator|)
name|XContentMapValues
operator|.
name|extractValue
argument_list|(
literal|"hits.total"
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDocs1
argument_list|,
name|numDocs2
argument_list|)
expr_stmt|;
name|searchRequestBody
operator|=
literal|"{ \"query\": { \"exists\" : {\"field\": \"field.with.dots\"} }}"
expr_stmt|;
name|response
operator|=
name|toMap
argument_list|(
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"GET"
argument_list|,
literal|"/"
operator|+
name|index
operator|+
literal|"/_search"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
operator|new
name|StringEntity
argument_list|(
name|searchRequestBody
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoFailures
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|numDocs2
operator|=
operator|(
name|int
operator|)
name|XContentMapValues
operator|.
name|extractValue
argument_list|(
literal|"hits.total"
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDocs1
argument_list|,
name|numDocs2
argument_list|)
expr_stmt|;
block|}
DECL|method|toMap
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|toMap
parameter_list|(
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|XContentHelper
operator|.
name|convertToMap
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|EntityUtils
operator|.
name|toString
argument_list|(
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|assertNoFailures
specifier|static
name|void
name|assertNoFailures
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|response
parameter_list|)
block|{
name|int
name|failed
init|=
operator|(
name|int
operator|)
name|XContentMapValues
operator|.
name|extractValue
argument_list|(
literal|"_shards.failed"
argument_list|,
name|response
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|failed
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests that a single document survives. Super basic smoke test.      */
DECL|method|testSingleDoc
specifier|public
name|void
name|testSingleDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|docLocation
init|=
literal|"/"
operator|+
name|getTestName
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|+
literal|"/doc/1"
decl_stmt|;
name|String
name|doc
init|=
literal|"{\"test\": \"test\"}"
decl_stmt|;
if|if
condition|(
name|runningAgainstOldCluster
condition|)
block|{
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"PUT"
argument_list|,
name|docLocation
argument_list|,
name|singletonMap
argument_list|(
literal|"refresh"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
operator|new
name|StringEntity
argument_list|(
name|doc
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|EntityUtils
operator|.
name|toString
argument_list|(
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"GET"
argument_list|,
name|docLocation
argument_list|)
operator|.
name|getEntity
argument_list|()
argument_list|)
argument_list|,
name|containsString
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomDocumentsAndSnapshot
specifier|public
name|void
name|testRandomDocumentsAndSnapshot
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|testName
init|=
name|getTestName
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|String
name|index
init|=
name|testName
operator|+
literal|"_data"
decl_stmt|;
name|String
name|infoDocument
init|=
literal|"/"
operator|+
name|testName
operator|+
literal|"_info/doc/info"
decl_stmt|;
name|int
name|count
decl_stmt|;
name|boolean
name|shouldHaveTranslog
decl_stmt|;
if|if
condition|(
name|runningAgainstOldCluster
condition|)
block|{
name|count
operator|=
name|between
argument_list|(
literal|200
argument_list|,
literal|300
argument_list|)
expr_stmt|;
comment|/* We've had bugs in the past where we couldn't restore              * an index without a translog so we randomize whether              * or not we have one. */
name|shouldHaveTranslog
operator|=
name|randomBoolean
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Creating {} documents"
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|indexRandomDocuments
argument_list|(
name|index
argument_list|,
name|count
argument_list|,
literal|true
argument_list|,
name|i
lambda|->
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
expr_stmt|;
name|createSnapshot
argument_list|()
expr_stmt|;
comment|// Explicitly flush so we're sure to have a bunch of documents in the Lucene index
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"POST"
argument_list|,
literal|"/_flush"
argument_list|)
expr_stmt|;
if|if
condition|(
name|shouldHaveTranslog
condition|)
block|{
comment|// Update a few documents so we are sure to have a translog
name|indexRandomDocuments
argument_list|(
name|index
argument_list|,
name|count
operator|/
literal|10
argument_list|,
literal|false
comment|/* Flushing here would invalidate the whole thing....*/
argument_list|,
name|i
lambda|->
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Record how many documents we built so we can compare later
name|XContentBuilder
name|infoDoc
init|=
name|JsonXContent
operator|.
name|contentBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
decl_stmt|;
name|infoDoc
operator|.
name|field
argument_list|(
literal|"count"
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|infoDoc
operator|.
name|field
argument_list|(
literal|"should_have_translog"
argument_list|,
name|shouldHaveTranslog
argument_list|)
expr_stmt|;
name|infoDoc
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"PUT"
argument_list|,
name|infoDocument
argument_list|,
name|singletonMap
argument_list|(
literal|"refresh"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
operator|new
name|StringEntity
argument_list|(
name|infoDoc
operator|.
name|string
argument_list|()
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Load the number of documents that were written to the old cluster
name|String
name|doc
init|=
name|EntityUtils
operator|.
name|toString
argument_list|(
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"GET"
argument_list|,
name|infoDocument
argument_list|,
name|singletonMap
argument_list|(
literal|"filter_path"
argument_list|,
literal|"_source"
argument_list|)
argument_list|)
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\"count\":(\\d+)"
argument_list|)
operator|.
name|matcher
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|doc
argument_list|,
name|m
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
name|count
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\"should_have_translog\":(true|false)"
argument_list|)
operator|.
name|matcher
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
argument_list|,
name|m
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
name|shouldHaveTranslog
operator|=
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Count the documents in the index to make sure we have as many as we put there
name|String
name|countResponse
init|=
name|EntityUtils
operator|.
name|toString
argument_list|(
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"GET"
argument_list|,
literal|"/"
operator|+
name|index
operator|+
literal|"/_search"
argument_list|,
name|singletonMap
argument_list|(
literal|"size"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|countResponse
argument_list|,
name|containsString
argument_list|(
literal|"\"total\":"
operator|+
name|count
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
literal|false
operator|==
name|runningAgainstOldCluster
condition|)
block|{
name|assertTranslogRecoveryStatistics
argument_list|(
name|index
argument_list|,
name|shouldHaveTranslog
argument_list|)
expr_stmt|;
block|}
name|restoreSnapshot
argument_list|(
name|index
argument_list|,
name|count
argument_list|)
expr_stmt|;
comment|// TODO finish adding tests for the things in OldIndexBackwardsCompatibilityIT
block|}
comment|// TODO tests for upgrades after shrink. We've had trouble with shrink in the past.
DECL|method|indexRandomDocuments
specifier|private
name|void
name|indexRandomDocuments
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|count
parameter_list|,
name|boolean
name|flushAllowed
parameter_list|,
name|CheckedFunction
argument_list|<
name|Integer
argument_list|,
name|XContentBuilder
argument_list|,
name|IOException
argument_list|>
name|docSupplier
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Indexing document [{}]"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"POST"
argument_list|,
literal|"/"
operator|+
name|index
operator|+
literal|"/doc/"
operator|+
name|i
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
operator|new
name|StringEntity
argument_list|(
name|docSupplier
operator|.
name|apply
argument_list|(
name|i
argument_list|)
operator|.
name|string
argument_list|()
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Refreshing [{}]"
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"POST"
argument_list|,
literal|"/"
operator|+
name|index
operator|+
literal|"/_refresh"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|flushAllowed
operator|&&
name|rarely
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Flushing [{}]"
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"POST"
argument_list|,
literal|"/"
operator|+
name|index
operator|+
literal|"/_flush"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|createSnapshot
specifier|private
name|void
name|createSnapshot
parameter_list|()
throws|throws
name|IOException
block|{
name|XContentBuilder
name|repoConfig
init|=
name|JsonXContent
operator|.
name|contentBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
decl_stmt|;
block|{
name|repoConfig
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"fs"
argument_list|)
expr_stmt|;
name|repoConfig
operator|.
name|startObject
argument_list|(
literal|"settings"
argument_list|)
expr_stmt|;
block|{
name|repoConfig
operator|.
name|field
argument_list|(
literal|"compress"
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|repoConfig
operator|.
name|field
argument_list|(
literal|"location"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.path.repo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|repoConfig
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|repoConfig
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"PUT"
argument_list|,
name|REPO
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
operator|new
name|StringEntity
argument_list|(
name|repoConfig
operator|.
name|string
argument_list|()
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"PUT"
argument_list|,
name|REPO
operator|+
literal|"/snap"
argument_list|,
name|singletonMap
argument_list|(
literal|"wait_for_completion"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertTranslogRecoveryStatistics
specifier|private
name|void
name|assertTranslogRecoveryStatistics
parameter_list|(
name|String
name|index
parameter_list|,
name|boolean
name|shouldHaveTranslog
parameter_list|)
throws|throws
name|ParseException
throws|,
name|IOException
block|{
name|boolean
name|restoredFromTranslog
init|=
literal|false
decl_stmt|;
name|boolean
name|foundPrimary
init|=
literal|false
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"h"
argument_list|,
literal|"index,shard,type,stage,translog_ops_recovered"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"s"
argument_list|,
literal|"index,shard,type"
argument_list|)
expr_stmt|;
name|String
name|recoveryResponse
init|=
name|EntityUtils
operator|.
name|toString
argument_list|(
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"GET"
argument_list|,
literal|"/_cat/recovery/"
operator|+
name|index
argument_list|,
name|params
argument_list|)
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|recoveryResponse
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
control|)
block|{
comment|// Find the primaries
name|foundPrimary
operator|=
literal|true
expr_stmt|;
if|if
condition|(
literal|false
operator|==
name|line
operator|.
name|contains
argument_list|(
literal|"done"
argument_list|)
operator|&&
name|line
operator|.
name|contains
argument_list|(
literal|"existing_store"
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|/* Mark if we see a primary that looked like it restored from the translog.              * Not all primaries will look like this all the time because we modify              * random documents when we want there to be a translog and they might              * not be spread around all the shards. */
name|Matcher
name|m
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(\\d+)$"
argument_list|)
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|line
argument_list|,
name|m
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|translogOps
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|translogOps
operator|>
literal|0
condition|)
block|{
name|restoredFromTranslog
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"expected to find a primary but didn't\n"
operator|+
name|recoveryResponse
argument_list|,
name|foundPrimary
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"mismatch while checking for translog recovery\n"
operator|+
name|recoveryResponse
argument_list|,
name|shouldHaveTranslog
argument_list|,
name|restoredFromTranslog
argument_list|)
expr_stmt|;
name|String
name|currentLuceneVersion
init|=
name|Version
operator|.
name|CURRENT
operator|.
name|luceneVersion
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|bwcLuceneVersion
init|=
name|oldClusterVersion
operator|.
name|luceneVersion
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|shouldHaveTranslog
operator|&&
literal|false
operator|==
name|currentLuceneVersion
operator|.
name|equals
argument_list|(
name|bwcLuceneVersion
argument_list|)
condition|)
block|{
name|int
name|numCurrentVersion
init|=
literal|0
decl_stmt|;
name|int
name|numBwcVersion
init|=
literal|0
decl_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"h"
argument_list|,
literal|"prirep,shard,index,version"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"s"
argument_list|,
literal|"prirep,shard,index"
argument_list|)
expr_stmt|;
name|String
name|segmentsResponse
init|=
name|EntityUtils
operator|.
name|toString
argument_list|(
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"GET"
argument_list|,
literal|"/_cat/segments/"
operator|+
name|index
argument_list|,
name|params
argument_list|)
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|segmentsResponse
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
control|)
block|{
if|if
condition|(
literal|false
operator|==
name|line
operator|.
name|startsWith
argument_list|(
literal|"p"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|Matcher
name|m
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(\\d+\\.\\d+\\.\\d+)$"
argument_list|)
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|line
argument_list|,
name|m
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|version
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentLuceneVersion
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
name|numCurrentVersion
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bwcLuceneVersion
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
name|numBwcVersion
operator|++
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"expected version to be one of ["
operator|+
name|currentLuceneVersion
operator|+
literal|","
operator|+
name|bwcLuceneVersion
operator|+
literal|"] but was "
operator|+
name|line
argument_list|)
expr_stmt|;
block|}
block|}
name|assertNotEquals
argument_list|(
literal|"expected at least 1 current segment after translog recovery"
argument_list|,
literal|0
argument_list|,
name|numCurrentVersion
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
literal|"expected at least 1 old segment"
argument_list|,
literal|0
argument_list|,
name|numBwcVersion
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|restoreSnapshot
specifier|private
name|void
name|restoreSnapshot
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|ParseException
throws|,
name|IOException
block|{
if|if
condition|(
literal|false
operator|==
name|runningAgainstOldCluster
condition|)
block|{
comment|/* Remove any "restored" indices from the old cluster run of this test.              * We intentionally don't remove them while running this against the              * old cluster so we can test starting the node with a restored index              * in the cluster. */
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"DELETE"
argument_list|,
literal|"/restored_*"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|runningAgainstOldCluster
condition|)
block|{
comment|// TODO restoring the snapshot seems to fail! This seems like a bug.
name|XContentBuilder
name|restoreCommand
init|=
name|JsonXContent
operator|.
name|contentBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
decl_stmt|;
name|restoreCommand
operator|.
name|field
argument_list|(
literal|"include_global_state"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|restoreCommand
operator|.
name|field
argument_list|(
literal|"indices"
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|restoreCommand
operator|.
name|field
argument_list|(
literal|"rename_pattern"
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|restoreCommand
operator|.
name|field
argument_list|(
literal|"rename_replacement"
argument_list|,
literal|"restored_"
operator|+
name|index
argument_list|)
expr_stmt|;
name|restoreCommand
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"POST"
argument_list|,
name|REPO
operator|+
literal|"/snap/_restore"
argument_list|,
name|singletonMap
argument_list|(
literal|"wait_for_completion"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
operator|new
name|StringEntity
argument_list|(
name|restoreCommand
operator|.
name|string
argument_list|()
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|countResponse
init|=
name|EntityUtils
operator|.
name|toString
argument_list|(
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"GET"
argument_list|,
literal|"/restored_"
operator|+
name|index
operator|+
literal|"/_search"
argument_list|,
name|singletonMap
argument_list|(
literal|"size"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|countResponse
argument_list|,
name|containsString
argument_list|(
literal|"\"total\":"
operator|+
name|count
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|randomLenientBoolean
specifier|private
name|Object
name|randomLenientBoolean
parameter_list|()
block|{
return|return
name|randomFrom
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"off"
block|,
literal|"no"
block|,
literal|"0"
block|,
literal|0
block|,
literal|"false"
block|,
literal|false
block|,
literal|"on"
block|,
literal|"yes"
block|,
literal|"1"
block|,
literal|1
block|,
literal|"true"
block|,
literal|true
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

