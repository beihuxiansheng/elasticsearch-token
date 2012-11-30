begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.index.mapper.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|xcontent
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|bytes
operator|.
name|BytesReference
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
name|index
operator|.
name|Index
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
name|analysis
operator|.
name|AnalysisService
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
name|mapper
operator|.
name|DocumentMapper
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
name|mapper
operator|.
name|DocumentMapperParser
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
name|mapper
operator|.
name|MapperParsingException
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
name|mapper
operator|.
name|attachment
operator|.
name|AttachmentMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
operator|.
name|Test
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|Streams
operator|.
name|copyToBytesFromClasspath
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
name|io
operator|.
name|Streams
operator|.
name|copyToStringFromClasspath
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
name|*
import|;
end_import

begin_comment
comment|/**  * Test for https://github.com/elasticsearch/elasticsearch-mapper-attachments/issues/38  */
end_comment

begin_class
DECL|class|MetadataMapperTest
specifier|public
class|class
name|MetadataMapperTest
block|{
DECL|method|checkDate
specifier|protected
name|void
name|checkDate
parameter_list|(
name|String
name|filename
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|Long
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|DocumentMapperParser
name|mapperParser
init|=
operator|new
name|DocumentMapperParser
argument_list|(
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
argument_list|,
name|settings
argument_list|,
operator|new
name|AnalysisService
argument_list|(
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|mapperParser
operator|.
name|putTypeParser
argument_list|(
name|AttachmentMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|AttachmentMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|mapping
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/xcontent/test-mapping.json"
argument_list|)
decl_stmt|;
name|DocumentMapper
name|docMapper
init|=
name|mapperParser
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|byte
index|[]
name|html
init|=
name|copyToBytesFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/xcontent/"
operator|+
name|filename
argument_list|)
decl_stmt|;
name|BytesReference
name|json
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"_id"
argument_list|,
literal|1
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"file"
argument_list|)
operator|.
name|field
argument_list|(
literal|"_name"
argument_list|,
literal|"htmlWithoutDateMeta.html"
argument_list|)
operator|.
name|field
argument_list|(
literal|"content"
argument_list|,
name|html
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|docMapper
operator|.
name|parse
argument_list|(
name|json
argument_list|)
operator|.
name|rootDoc
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartName
argument_list|(
literal|"file"
argument_list|)
operator|.
name|mapper
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"World"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartName
argument_list|(
literal|"file.name"
argument_list|)
operator|.
name|mapper
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"htmlWithoutDateMeta.html"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|expected
operator|==
literal|null
condition|)
block|{
name|assertThat
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartName
argument_list|(
literal|"file.date"
argument_list|)
operator|.
name|mapper
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartName
argument_list|(
literal|"file.date"
argument_list|)
operator|.
name|mapper
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartName
argument_list|(
literal|"file.title"
argument_list|)
operator|.
name|mapper
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"Hello"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartName
argument_list|(
literal|"file.author"
argument_list|)
operator|.
name|mapper
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"kimchy"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartName
argument_list|(
literal|"file.keywords"
argument_list|)
operator|.
name|mapper
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"elasticsearch,cool,bonsai"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartName
argument_list|(
literal|"file.content_type"
argument_list|)
operator|.
name|mapper
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"text/html; charset=ISO-8859-1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIgnoreWithoutDate
specifier|public
name|void
name|testIgnoreWithoutDate
parameter_list|()
throws|throws
name|Exception
block|{
name|checkDate
argument_list|(
literal|"htmlWithoutDateMeta.html"
argument_list|,
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIgnoreWithEmptyDate
specifier|public
name|void
name|testIgnoreWithEmptyDate
parameter_list|()
throws|throws
name|Exception
block|{
name|checkDate
argument_list|(
literal|"htmlWithEmptyDateMeta.html"
argument_list|,
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIgnoreWithCorrectDate
specifier|public
name|void
name|testIgnoreWithCorrectDate
parameter_list|()
throws|throws
name|Exception
block|{
name|checkDate
argument_list|(
literal|"htmlWithValidDateMeta.html"
argument_list|,
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|,
literal|1354233600000L
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithoutDate
specifier|public
name|void
name|testWithoutDate
parameter_list|()
throws|throws
name|Exception
block|{
name|checkDate
argument_list|(
literal|"htmlWithoutDateMeta.html"
argument_list|,
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.mapping.attachment.ignore_errors"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expectedExceptions
operator|=
name|MapperParsingException
operator|.
name|class
argument_list|)
DECL|method|testWithEmptyDate
specifier|public
name|void
name|testWithEmptyDate
parameter_list|()
throws|throws
name|Exception
block|{
name|checkDate
argument_list|(
literal|"htmlWithEmptyDateMeta.html"
argument_list|,
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.mapping.attachment.ignore_errors"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithCorrectDate
specifier|public
name|void
name|testWithCorrectDate
parameter_list|()
throws|throws
name|Exception
block|{
name|checkDate
argument_list|(
literal|"htmlWithValidDateMeta.html"
argument_list|,
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.mapping.attachment.ignore_errors"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|1354233600000L
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

