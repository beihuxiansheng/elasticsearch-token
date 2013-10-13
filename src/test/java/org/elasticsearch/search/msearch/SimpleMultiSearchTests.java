begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.search.msearch
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|msearch
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|MultiSearchResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
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
name|AbstractIntegrationTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|*
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
name|equalTo
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|SimpleMultiSearchTests
specifier|public
class|class
name|SimpleMultiSearchTests
extends|extends
name|AbstractIntegrationTest
block|{
annotation|@
name|Test
DECL|method|simpleMultiSearch
specifier|public
name|void
name|simpleMultiSearch
parameter_list|()
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"xxx"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"yyy"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|MultiSearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareMultiSearch
argument_list|()
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"field"
argument_list|,
literal|"xxx"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"field"
argument_list|,
literal|"yyy"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
for|for
control|(
name|MultiSearchResponse
operator|.
name|Item
name|item
range|:
name|response
control|)
block|{
name|assertNoFailures
argument_list|(
name|item
operator|.
name|getResponse
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|0
index|]
operator|.
name|getResponse
argument_list|()
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|1
index|]
operator|.
name|getResponse
argument_list|()
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|2
index|]
operator|.
name|getResponse
argument_list|()
argument_list|,
literal|2l
argument_list|)
expr_stmt|;
name|assertFirstHit
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|0
index|]
operator|.
name|getResponse
argument_list|()
argument_list|,
name|hasId
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFirstHit
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|1
index|]
operator|.
name|getResponse
argument_list|()
argument_list|,
name|hasId
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

