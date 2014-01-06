begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * An http response that is built on top of {@link org.apache.lucene.util.BytesRef}.  *<p/>  *<p>Note, this class assumes that the utf8 result is not thread safe.  */
end_comment

begin_class
DECL|class|Utf8RestResponse
specifier|public
class|class
name|Utf8RestResponse
extends|extends
name|AbstractRestResponse
implements|implements
name|RestResponse
block|{
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|BytesRef
name|EMPTY
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|status
specifier|private
specifier|final
name|RestStatus
name|status
decl_stmt|;
DECL|field|utf8Result
specifier|private
specifier|final
name|BytesRef
name|utf8Result
decl_stmt|;
DECL|field|prefixUtf8Result
specifier|private
specifier|final
name|BytesRef
name|prefixUtf8Result
decl_stmt|;
DECL|field|suffixUtf8Result
specifier|private
specifier|final
name|BytesRef
name|suffixUtf8Result
decl_stmt|;
DECL|method|Utf8RestResponse
specifier|public
name|Utf8RestResponse
parameter_list|(
name|RestStatus
name|status
parameter_list|)
block|{
name|this
argument_list|(
name|status
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
block|}
DECL|method|Utf8RestResponse
specifier|public
name|Utf8RestResponse
parameter_list|(
name|RestStatus
name|status
parameter_list|,
name|BytesRef
name|utf8Result
parameter_list|)
block|{
name|this
argument_list|(
name|status
argument_list|,
name|utf8Result
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|Utf8RestResponse
specifier|public
name|Utf8RestResponse
parameter_list|(
name|RestStatus
name|status
parameter_list|,
name|BytesRef
name|utf8Result
parameter_list|,
name|BytesRef
name|prefixUtf8Result
parameter_list|,
name|BytesRef
name|suffixUtf8Result
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
name|this
operator|.
name|utf8Result
operator|=
name|utf8Result
expr_stmt|;
name|this
operator|.
name|prefixUtf8Result
operator|=
name|prefixUtf8Result
expr_stmt|;
name|this
operator|.
name|suffixUtf8Result
operator|=
name|suffixUtf8Result
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|contentThreadSafe
specifier|public
name|boolean
name|contentThreadSafe
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|contentType
specifier|public
name|String
name|contentType
parameter_list|()
block|{
return|return
literal|"text/plain; charset=UTF-8"
return|;
block|}
annotation|@
name|Override
DECL|method|content
specifier|public
name|byte
index|[]
name|content
parameter_list|()
block|{
return|return
name|utf8Result
operator|.
name|bytes
return|;
block|}
annotation|@
name|Override
DECL|method|contentLength
specifier|public
name|int
name|contentLength
parameter_list|()
block|{
return|return
name|utf8Result
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|contentOffset
specifier|public
name|int
name|contentOffset
parameter_list|()
block|{
return|return
name|utf8Result
operator|.
name|offset
return|;
block|}
annotation|@
name|Override
DECL|method|status
specifier|public
name|RestStatus
name|status
parameter_list|()
block|{
return|return
name|status
return|;
block|}
annotation|@
name|Override
DECL|method|prefixContent
specifier|public
name|byte
index|[]
name|prefixContent
parameter_list|()
block|{
return|return
name|prefixUtf8Result
operator|!=
literal|null
condition|?
name|prefixUtf8Result
operator|.
name|bytes
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|prefixContentLength
specifier|public
name|int
name|prefixContentLength
parameter_list|()
block|{
return|return
name|prefixUtf8Result
operator|!=
literal|null
condition|?
name|prefixUtf8Result
operator|.
name|length
else|:
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|prefixContentOffset
specifier|public
name|int
name|prefixContentOffset
parameter_list|()
block|{
return|return
name|prefixUtf8Result
operator|!=
literal|null
condition|?
name|prefixUtf8Result
operator|.
name|offset
else|:
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|suffixContent
specifier|public
name|byte
index|[]
name|suffixContent
parameter_list|()
block|{
return|return
name|suffixUtf8Result
operator|!=
literal|null
condition|?
name|suffixUtf8Result
operator|.
name|bytes
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|suffixContentLength
specifier|public
name|int
name|suffixContentLength
parameter_list|()
block|{
return|return
name|suffixUtf8Result
operator|!=
literal|null
condition|?
name|suffixUtf8Result
operator|.
name|length
else|:
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|suffixContentOffset
specifier|public
name|int
name|suffixContentOffset
parameter_list|()
block|{
return|return
name|suffixUtf8Result
operator|!=
literal|null
condition|?
name|suffixUtf8Result
operator|.
name|offset
else|:
literal|0
return|;
block|}
block|}
end_class

end_unit

