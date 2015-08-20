begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.lookup
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|lookup
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
name|payloads
operator|.
name|PayloadHelper
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
name|BytesRef
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
name|CharsRefBuilder
import|;
end_import

begin_class
DECL|class|TermPosition
specifier|public
class|class
name|TermPosition
block|{
DECL|field|position
specifier|public
name|int
name|position
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|startOffset
specifier|public
name|int
name|startOffset
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|endOffset
specifier|public
name|int
name|endOffset
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|payload
specifier|public
name|BytesRef
name|payload
decl_stmt|;
DECL|field|spare
specifier|private
name|CharsRefBuilder
name|spare
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
DECL|method|payloadAsString
specifier|public
name|String
name|payloadAsString
parameter_list|()
block|{
if|if
condition|(
name|payload
operator|!=
literal|null
operator|&&
name|payload
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|spare
operator|.
name|copyUTF8Bytes
argument_list|(
name|payload
argument_list|)
expr_stmt|;
return|return
name|spare
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|payloadAsFloat
specifier|public
name|float
name|payloadAsFloat
parameter_list|(
name|float
name|defaultMissing
parameter_list|)
block|{
if|if
condition|(
name|payload
operator|!=
literal|null
operator|&&
name|payload
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
return|return
name|PayloadHelper
operator|.
name|decodeFloat
argument_list|(
name|payload
operator|.
name|bytes
argument_list|,
name|payload
operator|.
name|offset
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|defaultMissing
return|;
block|}
block|}
DECL|method|payloadAsInt
specifier|public
name|int
name|payloadAsInt
parameter_list|(
name|int
name|defaultMissing
parameter_list|)
block|{
if|if
condition|(
name|payload
operator|!=
literal|null
operator|&&
name|payload
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
return|return
name|PayloadHelper
operator|.
name|decodeInt
argument_list|(
name|payload
operator|.
name|bytes
argument_list|,
name|payload
operator|.
name|offset
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|defaultMissing
return|;
block|}
block|}
block|}
end_class

end_unit
