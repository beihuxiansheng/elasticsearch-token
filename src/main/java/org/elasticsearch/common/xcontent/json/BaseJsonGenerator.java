begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent.json
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|json
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonGenerator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|base
operator|.
name|GeneratorBase
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|util
operator|.
name|JsonGeneratorDelegate
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
name|io
operator|.
name|Streams
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_class
DECL|class|BaseJsonGenerator
specifier|public
class|class
name|BaseJsonGenerator
extends|extends
name|JsonGeneratorDelegate
block|{
DECL|field|base
specifier|protected
specifier|final
name|GeneratorBase
name|base
decl_stmt|;
DECL|method|BaseJsonGenerator
specifier|public
name|BaseJsonGenerator
parameter_list|(
name|JsonGenerator
name|generator
parameter_list|,
name|JsonGenerator
name|base
parameter_list|)
block|{
name|super
argument_list|(
name|generator
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|base
operator|instanceof
name|GeneratorBase
condition|)
block|{
name|this
operator|.
name|base
operator|=
operator|(
name|GeneratorBase
operator|)
name|base
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|base
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|BaseJsonGenerator
specifier|public
name|BaseJsonGenerator
parameter_list|(
name|JsonGenerator
name|generator
parameter_list|)
block|{
name|this
argument_list|(
name|generator
argument_list|,
name|generator
argument_list|)
expr_stmt|;
block|}
DECL|method|writeStartRaw
specifier|protected
name|void
name|writeStartRaw
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|writeRaw
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
DECL|method|writeEndRaw
specifier|public
name|void
name|writeEndRaw
parameter_list|()
block|{
assert|assert
name|base
operator|!=
literal|null
operator|:
literal|"JsonGenerator should be of instance GeneratorBase but was: "
operator|+
name|delegate
operator|.
name|getClass
argument_list|()
assert|;
if|if
condition|(
name|base
operator|!=
literal|null
condition|)
block|{
name|base
operator|.
name|getOutputContext
argument_list|()
operator|.
name|writeValue
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|writeRawValue
specifier|protected
name|void
name|writeRawValue
parameter_list|(
name|byte
index|[]
name|content
parameter_list|,
name|OutputStream
name|bos
parameter_list|)
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
name|bos
operator|.
name|write
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
DECL|method|writeRawValue
specifier|protected
name|void
name|writeRawValue
parameter_list|(
name|byte
index|[]
name|content
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|OutputStream
name|bos
parameter_list|)
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
name|bos
operator|.
name|write
argument_list|(
name|content
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|writeRawValue
specifier|protected
name|void
name|writeRawValue
parameter_list|(
name|InputStream
name|content
parameter_list|,
name|OutputStream
name|bos
parameter_list|)
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
name|Streams
operator|.
name|copy
argument_list|(
name|content
argument_list|,
name|bos
argument_list|)
expr_stmt|;
block|}
DECL|method|writeRawValue
specifier|protected
name|void
name|writeRawValue
parameter_list|(
name|BytesReference
name|content
parameter_list|,
name|OutputStream
name|bos
parameter_list|)
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
name|content
operator|.
name|writeTo
argument_list|(
name|bos
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

