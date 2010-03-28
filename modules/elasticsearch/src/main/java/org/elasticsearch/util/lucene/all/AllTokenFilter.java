begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.lucene.all
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|lucene
operator|.
name|all
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|TokenFilter
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PayloadAttribute
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
name|index
operator|.
name|Payload
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
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|payloads
operator|.
name|PayloadHelper
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|AllTokenFilter
specifier|public
class|class
name|AllTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|method|allTokenStream
specifier|public
specifier|static
name|TokenStream
name|allTokenStream
parameter_list|(
name|String
name|allFieldName
parameter_list|,
name|AllEntries
name|allEntries
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|AllTokenFilter
argument_list|(
name|analyzer
operator|.
name|reusableTokenStream
argument_list|(
name|allFieldName
argument_list|,
name|allEntries
argument_list|)
argument_list|,
name|allEntries
argument_list|)
return|;
block|}
DECL|field|allEntries
specifier|private
specifier|final
name|AllEntries
name|allEntries
decl_stmt|;
DECL|field|payloadAttribute
specifier|private
specifier|final
name|PayloadAttribute
name|payloadAttribute
decl_stmt|;
DECL|method|AllTokenFilter
name|AllTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|AllEntries
name|allEntries
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|allEntries
operator|=
name|allEntries
expr_stmt|;
name|payloadAttribute
operator|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|allEntries
specifier|public
name|AllEntries
name|allEntries
parameter_list|()
block|{
return|return
name|allEntries
return|;
block|}
DECL|method|incrementToken
annotation|@
name|Override
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|float
name|boost
init|=
name|allEntries
operator|.
name|current
argument_list|()
operator|.
name|boost
argument_list|()
decl_stmt|;
if|if
condition|(
name|boost
operator|!=
literal|1.0f
condition|)
block|{
name|payloadAttribute
operator|.
name|setPayload
argument_list|(
operator|new
name|Payload
argument_list|(
name|encodeFloat
argument_list|(
name|boost
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|payloadAttribute
operator|.
name|setPayload
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|toString
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|allEntries
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

