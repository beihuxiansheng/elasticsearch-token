begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.json
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|json
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
name|*
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
name|MapperCompressionException
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
name|SourceFieldMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|compression
operator|.
name|Compressor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|compression
operator|.
name|ZipCompressor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|lucene
operator|.
name|Lucene
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

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|JsonSourceFieldMapper
specifier|public
class|class
name|JsonSourceFieldMapper
extends|extends
name|JsonFieldMapper
argument_list|<
name|String
argument_list|>
implements|implements
name|SourceFieldMapper
block|{
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
extends|extends
name|JsonFieldMapper
operator|.
name|Defaults
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|SourceFieldMapper
operator|.
name|NAME
decl_stmt|;
DECL|field|ENABLED
specifier|public
specifier|static
specifier|final
name|boolean
name|ENABLED
init|=
literal|true
decl_stmt|;
DECL|field|INDEX
specifier|public
specifier|static
specifier|final
name|Field
operator|.
name|Index
name|INDEX
init|=
name|Field
operator|.
name|Index
operator|.
name|NO
decl_stmt|;
DECL|field|STORE
specifier|public
specifier|static
specifier|final
name|Field
operator|.
name|Store
name|STORE
init|=
name|Field
operator|.
name|Store
operator|.
name|YES
decl_stmt|;
DECL|field|OMIT_NORMS
specifier|public
specifier|static
specifier|final
name|boolean
name|OMIT_NORMS
init|=
literal|true
decl_stmt|;
DECL|field|OMIT_TERM_FREQ_AND_POSITIONS
specifier|public
specifier|static
specifier|final
name|boolean
name|OMIT_TERM_FREQ_AND_POSITIONS
init|=
literal|true
decl_stmt|;
DECL|field|COMPRESSOR
specifier|public
specifier|static
specifier|final
name|Compressor
name|COMPRESSOR
init|=
operator|new
name|ZipCompressor
argument_list|()
decl_stmt|;
DECL|field|NO_COMPRESSION
specifier|public
specifier|static
specifier|final
name|int
name|NO_COMPRESSION
init|=
operator|-
literal|1
decl_stmt|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|JsonMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|JsonSourceFieldMapper
argument_list|>
block|{
DECL|field|enabled
specifier|private
name|boolean
name|enabled
init|=
name|Defaults
operator|.
name|ENABLED
decl_stmt|;
DECL|field|compressor
specifier|private
name|Compressor
name|compressor
init|=
name|Defaults
operator|.
name|COMPRESSOR
decl_stmt|;
DECL|field|compressionThreshold
specifier|private
name|int
name|compressionThreshold
init|=
name|Defaults
operator|.
name|NO_COMPRESSION
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|()
block|{
name|super
argument_list|(
name|Defaults
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
comment|// source is always enabled for now
comment|//        public Builder enabled(boolean enabled) {
comment|//            this.enabled = enabled;
comment|//            return this;
comment|//        }
DECL|method|compressor
specifier|public
name|Builder
name|compressor
parameter_list|(
name|Compressor
name|compressor
parameter_list|)
block|{
name|this
operator|.
name|compressor
operator|=
name|compressor
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|compressionThreshold
specifier|public
name|Builder
name|compressionThreshold
parameter_list|(
name|int
name|compressionThreshold
parameter_list|)
block|{
name|this
operator|.
name|compressionThreshold
operator|=
name|compressionThreshold
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
annotation|@
name|Override
specifier|public
name|JsonSourceFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|JsonSourceFieldMapper
argument_list|(
name|name
argument_list|,
name|enabled
argument_list|,
name|compressionThreshold
argument_list|,
name|compressor
argument_list|)
return|;
block|}
block|}
DECL|field|enabled
specifier|private
specifier|final
name|boolean
name|enabled
decl_stmt|;
DECL|field|compressor
specifier|private
specifier|final
name|Compressor
name|compressor
decl_stmt|;
comment|// the size of the source file that we will perform compression for
DECL|field|compressionThreshold
specifier|private
specifier|final
name|int
name|compressionThreshold
decl_stmt|;
DECL|field|fieldSelector
specifier|private
specifier|final
name|SourceFieldSelector
name|fieldSelector
decl_stmt|;
DECL|method|JsonSourceFieldMapper
specifier|protected
name|JsonSourceFieldMapper
parameter_list|()
block|{
name|this
argument_list|(
name|Defaults
operator|.
name|NAME
argument_list|,
name|Defaults
operator|.
name|ENABLED
argument_list|)
expr_stmt|;
block|}
DECL|method|JsonSourceFieldMapper
specifier|protected
name|JsonSourceFieldMapper
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|enabled
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|enabled
argument_list|,
name|Defaults
operator|.
name|NO_COMPRESSION
argument_list|,
name|Defaults
operator|.
name|COMPRESSOR
argument_list|)
expr_stmt|;
block|}
DECL|method|JsonSourceFieldMapper
specifier|protected
name|JsonSourceFieldMapper
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|enabled
parameter_list|,
name|int
name|compressionThreshold
parameter_list|,
name|Compressor
name|compressor
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|name
argument_list|,
name|name
argument_list|,
name|Defaults
operator|.
name|INDEX
argument_list|,
name|Defaults
operator|.
name|STORE
argument_list|,
name|Defaults
operator|.
name|TERM_VECTOR
argument_list|,
name|Defaults
operator|.
name|BOOST
argument_list|,
name|Defaults
operator|.
name|OMIT_NORMS
argument_list|,
name|Defaults
operator|.
name|OMIT_TERM_FREQ_AND_POSITIONS
argument_list|,
name|Lucene
operator|.
name|KEYWORD_ANALYZER
argument_list|,
name|Lucene
operator|.
name|KEYWORD_ANALYZER
argument_list|)
expr_stmt|;
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
name|this
operator|.
name|compressionThreshold
operator|=
name|compressionThreshold
expr_stmt|;
name|this
operator|.
name|compressor
operator|=
name|compressor
expr_stmt|;
name|this
operator|.
name|fieldSelector
operator|=
operator|new
name|SourceFieldSelector
argument_list|(
name|indexName
argument_list|)
expr_stmt|;
block|}
DECL|method|enabled
specifier|public
name|boolean
name|enabled
parameter_list|()
block|{
return|return
name|this
operator|.
name|enabled
return|;
block|}
DECL|method|fieldSelector
specifier|public
name|FieldSelector
name|fieldSelector
parameter_list|()
block|{
return|return
name|this
operator|.
name|fieldSelector
return|;
block|}
DECL|method|parseCreateField
annotation|@
name|Override
specifier|protected
name|Field
name|parseCreateField
parameter_list|(
name|JsonParseContext
name|jsonContext
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Field
name|sourceField
decl_stmt|;
if|if
condition|(
name|compressionThreshold
operator|==
name|Defaults
operator|.
name|NO_COMPRESSION
operator|||
name|jsonContext
operator|.
name|source
argument_list|()
operator|.
name|length
argument_list|()
operator|<
name|compressionThreshold
condition|)
block|{
name|sourceField
operator|=
operator|new
name|Field
argument_list|(
name|name
argument_list|,
name|jsonContext
operator|.
name|source
argument_list|()
argument_list|,
name|store
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|sourceField
operator|=
operator|new
name|Field
argument_list|(
name|name
argument_list|,
name|compressor
operator|.
name|compressString
argument_list|(
name|jsonContext
operator|.
name|source
argument_list|()
argument_list|)
argument_list|,
name|store
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MapperCompressionException
argument_list|(
literal|"Failed to compress data"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|sourceField
return|;
block|}
DECL|method|value
annotation|@
name|Override
specifier|public
name|String
name|value
parameter_list|(
name|Document
name|document
parameter_list|)
block|{
name|Fieldable
name|field
init|=
name|document
operator|.
name|getFieldable
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
return|return
name|field
operator|==
literal|null
condition|?
literal|null
else|:
name|value
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|value
annotation|@
name|Override
specifier|public
name|String
name|value
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
if|if
condition|(
name|field
operator|.
name|stringValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|field
operator|.
name|stringValue
argument_list|()
return|;
block|}
name|byte
index|[]
name|compressed
init|=
name|field
operator|.
name|getBinaryValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|compressed
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
return|return
name|compressor
operator|.
name|decompressString
argument_list|(
name|compressed
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MapperCompressionException
argument_list|(
literal|"Failed to decompress data"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|valueAsString
annotation|@
name|Override
specifier|public
name|String
name|valueAsString
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
return|return
name|value
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|indexedValue
annotation|@
name|Override
specifier|public
name|String
name|indexedValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|value
return|;
block|}
DECL|class|SourceFieldSelector
specifier|private
specifier|static
class|class
name|SourceFieldSelector
implements|implements
name|FieldSelector
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|SourceFieldSelector
specifier|private
name|SourceFieldSelector
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|accept
annotation|@
name|Override
specifier|public
name|FieldSelectorResult
name|accept
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|FieldSelectorResult
operator|.
name|LOAD_AND_BREAK
return|;
block|}
return|return
name|FieldSelectorResult
operator|.
name|NO_LOAD
return|;
block|}
block|}
block|}
end_class

end_unit

