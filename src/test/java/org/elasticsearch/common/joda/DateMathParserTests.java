begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.common.joda
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|joda
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ElasticsearchTestCase
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
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
DECL|class|DateMathParserTests
specifier|public
class|class
name|DateMathParserTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|dataMathTests
specifier|public
name|void
name|dataMathTests
parameter_list|()
block|{
name|DateMathParser
name|parser
init|=
operator|new
name|DateMathParser
argument_list|(
name|Joda
operator|.
name|forPattern
argument_list|(
literal|"dateOptionalTime"
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"now"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"now+m"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"now+1m"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"now+11m"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|11
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"now+1d"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|TimeUnit
operator|.
name|DAYS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"now+1m+1s"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
operator|+
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"now+1m-1s"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
operator|-
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"now+1m+1s/m"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|parseUpperInclusive
argument_list|(
literal|"now+1m+1s/m"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"now+4y"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|TimeUnit
operator|.
name|DAYS
operator|.
name|toMillis
argument_list|(
literal|4
operator|*
literal|365
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|actualDateTests
specifier|public
name|void
name|actualDateTests
parameter_list|()
block|{
name|DateMathParser
name|parser
init|=
operator|new
name|DateMathParser
argument_list|(
name|Joda
operator|.
name|forPattern
argument_list|(
literal|"dateOptionalTime"
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"1970-01-01"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"1970-01-01||+1m"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"1970-01-01||+1m+1s"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
operator|+
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"2013-01-01||+1y"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"2013-01-01"
argument_list|,
literal|0
argument_list|)
operator|+
name|TimeUnit
operator|.
name|DAYS
operator|.
name|toMillis
argument_list|(
literal|365
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"2013-03-03||/y"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"2013-01-01"
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|parseUpperInclusive
argument_list|(
literal|"2013-03-03||/y"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"2014-01-01"
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

