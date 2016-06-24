begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Strings
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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
name|test
operator|.
name|rest
operator|.
name|client
operator|.
name|RestTestResponse
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
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Allows to cache the last obtained test response and or part of it within variables  * that can be used as input values in following requests and assertions.  */
end_comment

begin_class
DECL|class|Stash
specifier|public
class|class
name|Stash
implements|implements
name|ToXContent
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|Stash
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|Stash
name|EMPTY
init|=
operator|new
name|Stash
argument_list|()
decl_stmt|;
DECL|field|stash
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|stash
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|response
specifier|private
name|RestTestResponse
name|response
decl_stmt|;
comment|/**      * Allows to saved a specific field in the stash as key-value pair      */
DECL|method|stashValue
specifier|public
name|void
name|stashValue
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"stashing [{}]=[{}]"
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|Object
name|old
init|=
name|stash
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
operator|&&
name|old
operator|!=
name|value
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"replaced stashed value [{}] with same key [{}]"
argument_list|,
name|old
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|stashResponse
specifier|public
name|void
name|stashResponse
parameter_list|(
name|RestTestResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO we can almost certainly save time by lazily evaluating the body
name|stashValue
argument_list|(
literal|"body"
argument_list|,
name|response
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
block|}
comment|/**      * Clears the previously stashed values      */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|stash
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tells whether a particular value needs to be looked up in the stash      * The stash contains fields eventually extracted from previous responses that can be reused      * as arguments for following requests (e.g. scroll_id)      */
DECL|method|isStashedValue
specifier|public
name|boolean
name|isStashedValue
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|stashKey
init|=
name|key
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
name|Strings
operator|.
name|hasLength
argument_list|(
name|stashKey
argument_list|)
operator|&&
name|stashKey
operator|.
name|startsWith
argument_list|(
literal|"$"
argument_list|)
return|;
block|}
comment|/**      * Extracts a value from the current stash      * The stash contains fields eventually extracted from previous responses that can be reused      * as arguments for following requests (e.g. scroll_id)      */
DECL|method|unstashValue
specifier|public
name|Object
name|unstashValue
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"$body."
argument_list|)
condition|)
block|{
if|if
condition|(
name|response
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|response
operator|.
name|evaluate
argument_list|(
name|value
operator|.
name|substring
argument_list|(
literal|"$body"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|this
argument_list|)
return|;
block|}
name|Object
name|stashedValue
init|=
name|stash
operator|.
name|get
argument_list|(
name|value
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|stashedValue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"stashed value not found for key ["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|stashedValue
return|;
block|}
comment|/**      * Recursively unstashes map values if needed      */
DECL|method|unstashMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|unstashMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|copy
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|map
argument_list|)
decl_stmt|;
name|unstashObject
argument_list|(
name|copy
argument_list|)
expr_stmt|;
return|return
name|copy
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|unstashObject
specifier|private
name|void
name|unstashObject
parameter_list|(
name|Object
name|obj
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|obj
operator|instanceof
name|List
condition|)
block|{
name|List
name|list
init|=
operator|(
name|List
operator|)
name|obj
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
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|o
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|isStashedValue
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|list
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|unstashValue
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|unstashObject
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|obj
operator|instanceof
name|Map
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|(
name|Map
operator|)
name|obj
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|isStashedValue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
name|entry
operator|.
name|setValue
argument_list|(
name|unstashValue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|unstashObject
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"stash"
argument_list|,
name|stash
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

