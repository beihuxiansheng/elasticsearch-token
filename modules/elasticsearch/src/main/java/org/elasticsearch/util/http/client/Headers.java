begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright 2010 Ning, Inc.  *  * Ning licenses this file to you under the Apache License, version 2.0  * (the "License"); you may not use this file except in compliance with the  * License.  You may obtain a copy of the License at:  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the  * License for the specific language governing permissions and limitations  * under the License.  *  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.http.client
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|http
operator|.
name|client
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|http
operator|.
name|collection
operator|.
name|Pair
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_class
DECL|class|Headers
specifier|public
class|class
name|Headers
implements|implements
name|Iterable
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
block|{
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"Content-Type"
decl_stmt|;
DECL|field|headers
specifier|private
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|headers
init|=
operator|new
name|ArrayList
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|unmodifiableHeaders
specifier|public
specifier|static
name|Headers
name|unmodifiableHeaders
parameter_list|(
name|Headers
name|headers
parameter_list|)
block|{
return|return
operator|new
name|UnmodifiableHeaders
argument_list|(
name|headers
argument_list|)
return|;
block|}
DECL|method|Headers
specifier|public
name|Headers
parameter_list|()
block|{     }
DECL|method|Headers
specifier|public
name|Headers
parameter_list|(
name|Headers
name|src
parameter_list|)
block|{
if|if
condition|(
name|src
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|header
range|:
name|src
control|)
block|{
name|add
argument_list|(
name|header
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|Headers
specifier|public
name|Headers
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|headers
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|headers
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|String
name|value
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Adds the specified header and returns this headers object.      *      * @param name  The header name      * @param value The header value      * @return This object      */
DECL|method|add
specifier|public
name|Headers
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|headers
operator|.
name|add
argument_list|(
operator|new
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds the specified header and returns this headers object.      *      * @param header The name / value pair      * @return This object      */
DECL|method|add
specifier|public
name|Headers
name|add
parameter_list|(
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|header
parameter_list|)
block|{
name|headers
operator|.
name|add
argument_list|(
operator|new
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|header
operator|.
name|getFirst
argument_list|()
argument_list|,
name|header
operator|.
name|getSecond
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds all headers from the given headers object to this object and returns this headers object.      *      * @param srcHeaders The source headers object      * @return This object      */
DECL|method|addAll
specifier|public
name|Headers
name|addAll
parameter_list|(
name|Headers
name|srcHeaders
parameter_list|)
block|{
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|srcHeaders
operator|.
name|headers
control|)
block|{
name|headers
operator|.
name|add
argument_list|(
operator|new
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|entry
operator|.
name|getFirst
argument_list|()
argument_list|,
name|entry
operator|.
name|getSecond
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Convenience method to add a Content-type header      *      * @param contentType content type to set      * @return This object      */
DECL|method|addContentTypeHeader
specifier|public
name|Headers
name|addContentTypeHeader
parameter_list|(
name|String
name|contentType
parameter_list|)
block|{
return|return
name|add
argument_list|(
name|CONTENT_TYPE
argument_list|,
name|contentType
argument_list|)
return|;
block|}
comment|/**      * Replaces all existing headers with the header given.      *      * @param header The header name.      * @param value  The new header value.      */
DECL|method|replace
specifier|public
name|void
name|replace
parameter_list|(
specifier|final
name|String
name|header
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
block|{
name|remove
argument_list|(
name|header
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|header
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * {@inheritDoc}      */
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|headers
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**      * Returns the value of first header of the given name.      *      * @param name The header's name      * @return The value      */
DECL|method|getHeaderValue
specifier|public
name|String
name|getHeaderValue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|header
range|:
name|this
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equalsIgnoreCase
argument_list|(
name|header
operator|.
name|getFirst
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|header
operator|.
name|getSecond
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Returns the values of all header of the given name.      *      * @param name The header name      * @return The values, will not be<code>null</code>      */
DECL|method|getHeaderValues
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getHeaderValues
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|header
range|:
name|this
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equalsIgnoreCase
argument_list|(
name|header
operator|.
name|getFirst
argument_list|()
argument_list|)
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|header
operator|.
name|getSecond
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|values
return|;
block|}
comment|/**      * Adds the specified header(s) and returns this headers object.      *      * @param name The header name      * @return This object      */
DECL|method|remove
specifier|public
name|Headers
name|remove
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|it
init|=
name|headers
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|header
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equalsIgnoreCase
argument_list|(
name|header
operator|.
name|getFirst
argument_list|()
argument_list|)
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
specifier|final
name|Headers
name|other
init|=
operator|(
name|Headers
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|headers
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|headers
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|headers
operator|.
name|equals
argument_list|(
name|other
operator|.
name|headers
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
DECL|class|UnmodifiableHeaders
specifier|private
specifier|static
class|class
name|UnmodifiableHeaders
extends|extends
name|Headers
block|{
DECL|field|headers
specifier|final
name|Headers
name|headers
decl_stmt|;
DECL|method|UnmodifiableHeaders
name|UnmodifiableHeaders
parameter_list|(
name|Headers
name|headers
parameter_list|)
block|{
name|this
operator|.
name|headers
operator|=
name|headers
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|Headers
name|add
parameter_list|(
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|header
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|Headers
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|addAll
specifier|public
name|Headers
name|addAll
parameter_list|(
name|Headers
name|srcHeaders
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|addContentTypeHeader
specifier|public
name|Headers
name|addContentTypeHeader
parameter_list|(
name|String
name|contentType
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|headers
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getHeaderValue
specifier|public
name|String
name|getHeaderValue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|headers
operator|.
name|getHeaderValue
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getHeaderValues
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getHeaderValues
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|headers
operator|.
name|getHeaderValues
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|headers
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|Headers
name|remove
parameter_list|(
name|String
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

