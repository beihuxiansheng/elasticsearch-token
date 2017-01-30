begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|DiffableUtils
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
name|BytesArray
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
name|stream
operator|.
name|Writeable
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
name|ToXContent
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
name|XContentFactory
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
name|XContentParser
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
name|XContentType
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
name|AbstractSerializingTestCase
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
name|io
operator|.
name|UncheckedIOException
import|;
end_import

begin_class
DECL|class|ScriptMetaDataTests
specifier|public
class|class
name|ScriptMetaDataTests
extends|extends
name|AbstractSerializingTestCase
argument_list|<
name|ScriptMetaData
argument_list|>
block|{
DECL|method|testGetScript
specifier|public
name|void
name|testGetScript
parameter_list|()
throws|throws
name|Exception
block|{
name|ScriptMetaData
operator|.
name|Builder
name|builder
init|=
operator|new
name|ScriptMetaData
operator|.
name|Builder
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|XContentBuilder
name|sourceBuilder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
decl_stmt|;
name|sourceBuilder
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"template"
argument_list|)
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
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|storeScript
argument_list|(
literal|"template"
argument_list|,
name|StoredScriptSource
operator|.
name|parse
argument_list|(
literal|"lang"
argument_list|,
name|sourceBuilder
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sourceBuilder
operator|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
expr_stmt|;
name|sourceBuilder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"template"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|storeScript
argument_list|(
literal|"template_field"
argument_list|,
name|StoredScriptSource
operator|.
name|parse
argument_list|(
literal|"lang"
argument_list|,
name|sourceBuilder
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sourceBuilder
operator|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
expr_stmt|;
name|sourceBuilder
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"script"
argument_list|)
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
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|storeScript
argument_list|(
literal|"script"
argument_list|,
name|StoredScriptSource
operator|.
name|parse
argument_list|(
literal|"lang"
argument_list|,
name|sourceBuilder
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sourceBuilder
operator|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
expr_stmt|;
name|sourceBuilder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"script"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|storeScript
argument_list|(
literal|"script_field"
argument_list|,
name|StoredScriptSource
operator|.
name|parse
argument_list|(
literal|"lang"
argument_list|,
name|sourceBuilder
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sourceBuilder
operator|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
expr_stmt|;
name|sourceBuilder
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
expr_stmt|;
name|builder
operator|.
name|storeScript
argument_list|(
literal|"any"
argument_list|,
name|StoredScriptSource
operator|.
name|parse
argument_list|(
literal|"lang"
argument_list|,
name|sourceBuilder
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ScriptMetaData
name|scriptMetaData
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"field\":\"value\"}"
argument_list|,
name|scriptMetaData
operator|.
name|getStoredScript
argument_list|(
literal|"template"
argument_list|,
literal|"lang"
argument_list|)
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|scriptMetaData
operator|.
name|getStoredScript
argument_list|(
literal|"template_field"
argument_list|,
literal|"lang"
argument_list|)
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\"field\":\"value\"}"
argument_list|,
name|scriptMetaData
operator|.
name|getStoredScript
argument_list|(
literal|"script"
argument_list|,
literal|"lang"
argument_list|)
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|scriptMetaData
operator|.
name|getStoredScript
argument_list|(
literal|"script_field"
argument_list|,
literal|"lang"
argument_list|)
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\"field\":\"value\"}"
argument_list|,
name|scriptMetaData
operator|.
name|getStoredScript
argument_list|(
literal|"any"
argument_list|,
literal|"lang"
argument_list|)
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDiff
specifier|public
name|void
name|testDiff
parameter_list|()
throws|throws
name|Exception
block|{
name|ScriptMetaData
operator|.
name|Builder
name|builder
init|=
operator|new
name|ScriptMetaData
operator|.
name|Builder
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|builder
operator|.
name|storeScript
argument_list|(
literal|"1"
argument_list|,
name|StoredScriptSource
operator|.
name|parse
argument_list|(
literal|"lang"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|"{\"foo\":\"abc\"}"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|storeScript
argument_list|(
literal|"2"
argument_list|,
name|StoredScriptSource
operator|.
name|parse
argument_list|(
literal|"lang"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|"{\"foo\":\"def\"}"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|storeScript
argument_list|(
literal|"3"
argument_list|,
name|StoredScriptSource
operator|.
name|parse
argument_list|(
literal|"lang"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|"{\"foo\":\"ghi\"}"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ScriptMetaData
name|scriptMetaData1
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|builder
operator|=
operator|new
name|ScriptMetaData
operator|.
name|Builder
argument_list|(
name|scriptMetaData1
argument_list|)
expr_stmt|;
name|builder
operator|.
name|storeScript
argument_list|(
literal|"2"
argument_list|,
name|StoredScriptSource
operator|.
name|parse
argument_list|(
literal|"lang"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|"{\"foo\":\"changed\"}"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|deleteScript
argument_list|(
literal|"3"
argument_list|,
literal|"lang"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|storeScript
argument_list|(
literal|"4"
argument_list|,
name|StoredScriptSource
operator|.
name|parse
argument_list|(
literal|"lang"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|"{\"foo\":\"jkl\"}"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ScriptMetaData
name|scriptMetaData2
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|ScriptMetaData
operator|.
name|ScriptMetadataDiff
name|diff
init|=
operator|(
name|ScriptMetaData
operator|.
name|ScriptMetadataDiff
operator|)
name|scriptMetaData2
operator|.
name|diff
argument_list|(
name|scriptMetaData1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|DiffableUtils
operator|.
name|MapDiff
operator|)
name|diff
operator|.
name|pipelines
operator|)
operator|.
name|getDeletes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
operator|(
operator|(
name|DiffableUtils
operator|.
name|MapDiff
operator|)
name|diff
operator|.
name|pipelines
operator|)
operator|.
name|getDeletes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|DiffableUtils
operator|.
name|MapDiff
operator|)
name|diff
operator|.
name|pipelines
operator|)
operator|.
name|getDiffs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
operator|(
operator|(
name|DiffableUtils
operator|.
name|MapDiff
operator|)
name|diff
operator|.
name|pipelines
operator|)
operator|.
name|getDiffs
argument_list|()
operator|.
name|get
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|DiffableUtils
operator|.
name|MapDiff
operator|)
name|diff
operator|.
name|pipelines
operator|)
operator|.
name|getUpserts
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
operator|(
operator|(
name|DiffableUtils
operator|.
name|MapDiff
operator|)
name|diff
operator|.
name|pipelines
operator|)
operator|.
name|getUpserts
argument_list|()
operator|.
name|get
argument_list|(
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|ScriptMetaData
name|result
init|=
operator|(
name|ScriptMetaData
operator|)
name|diff
operator|.
name|apply
argument_list|(
name|scriptMetaData1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"foo\":\"abc\"}"
argument_list|,
name|result
operator|.
name|getStoredScript
argument_list|(
literal|"1"
argument_list|,
literal|"lang"
argument_list|)
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\"foo\":\"changed\"}"
argument_list|,
name|result
operator|.
name|getStoredScript
argument_list|(
literal|"2"
argument_list|,
literal|"lang"
argument_list|)
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\"foo\":\"jkl\"}"
argument_list|,
name|result
operator|.
name|getStoredScript
argument_list|(
literal|"4"
argument_list|,
literal|"lang"
argument_list|)
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testBuilder
specifier|public
name|void
name|testBuilder
parameter_list|()
block|{
name|ScriptMetaData
operator|.
name|Builder
name|builder
init|=
operator|new
name|ScriptMetaData
operator|.
name|Builder
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|builder
operator|.
name|storeScript
argument_list|(
literal|"_id"
argument_list|,
name|StoredScriptSource
operator|.
name|parse
argument_list|(
literal|"_lang"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|"{\"script\":\"1 + 1\"}"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ScriptMetaData
name|result
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1 + 1"
argument_list|,
name|result
operator|.
name|getStoredScript
argument_list|(
literal|"_id"
argument_list|,
literal|"_lang"
argument_list|)
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|randomScriptMetaData
specifier|private
name|ScriptMetaData
name|randomScriptMetaData
parameter_list|(
name|XContentType
name|sourceContentType
parameter_list|)
throws|throws
name|IOException
block|{
name|ScriptMetaData
operator|.
name|Builder
name|builder
init|=
operator|new
name|ScriptMetaData
operator|.
name|Builder
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|int
name|numScripts
init|=
name|scaledRandomIntBetween
argument_list|(
literal|0
argument_list|,
literal|32
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numScripts
condition|;
name|i
operator|++
control|)
block|{
name|String
name|lang
init|=
name|randomAsciiOfLength
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|XContentBuilder
name|sourceBuilder
init|=
name|XContentBuilder
operator|.
name|builder
argument_list|(
name|sourceContentType
operator|.
name|xContent
argument_list|()
argument_list|)
decl_stmt|;
name|sourceBuilder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"script"
argument_list|,
name|randomAsciiOfLength
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|storeScript
argument_list|(
name|randomAsciiOfLength
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|,
name|StoredScriptSource
operator|.
name|parse
argument_list|(
name|lang
argument_list|,
name|sourceBuilder
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createTestInstance
specifier|protected
name|ScriptMetaData
name|createTestInstance
parameter_list|()
block|{
try|try
block|{
return|return
name|randomScriptMetaData
argument_list|(
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|UncheckedIOException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|instanceReader
specifier|protected
name|Writeable
operator|.
name|Reader
argument_list|<
name|ScriptMetaData
argument_list|>
name|instanceReader
parameter_list|()
block|{
return|return
name|ScriptMetaData
operator|::
operator|new
return|;
block|}
annotation|@
name|Override
DECL|method|doParseInstance
specifier|protected
name|ScriptMetaData
name|doParseInstance
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
block|{
try|try
block|{
return|return
name|ScriptMetaData
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|UncheckedIOException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

