begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.translog
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|translog
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|TypeSafeMatcher
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
name|ArrayList
import|;
end_import

begin_class
DECL|class|SnapshotMatchers
specifier|public
specifier|final
class|class
name|SnapshotMatchers
block|{
DECL|method|SnapshotMatchers
specifier|private
name|SnapshotMatchers
parameter_list|()
block|{      }
comment|/**      * Consumes a snapshot and make sure it's size is as expected      */
DECL|method|size
specifier|public
specifier|static
name|Matcher
argument_list|<
name|Translog
operator|.
name|Snapshot
argument_list|>
name|size
parameter_list|(
name|int
name|size
parameter_list|)
block|{
return|return
operator|new
name|SizeMatcher
argument_list|(
name|size
argument_list|)
return|;
block|}
comment|/**      * Consumes a snapshot and make sure it's content is as expected      */
DECL|method|equalsTo
specifier|public
specifier|static
name|Matcher
argument_list|<
name|Translog
operator|.
name|Snapshot
argument_list|>
name|equalsTo
parameter_list|(
name|Translog
operator|.
name|Operation
modifier|...
name|ops
parameter_list|)
block|{
return|return
operator|new
name|EqualMatcher
argument_list|(
name|ops
argument_list|)
return|;
block|}
comment|/**      * Consumes a snapshot and make sure it's content is as expected      */
DECL|method|equalsTo
specifier|public
specifier|static
name|Matcher
argument_list|<
name|Translog
operator|.
name|Snapshot
argument_list|>
name|equalsTo
parameter_list|(
name|ArrayList
argument_list|<
name|Translog
operator|.
name|Operation
argument_list|>
name|ops
parameter_list|)
block|{
return|return
operator|new
name|EqualMatcher
argument_list|(
name|ops
operator|.
name|toArray
argument_list|(
operator|new
name|Translog
operator|.
name|Operation
index|[
name|ops
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
DECL|class|SizeMatcher
specifier|public
specifier|static
class|class
name|SizeMatcher
extends|extends
name|TypeSafeMatcher
argument_list|<
name|Translog
operator|.
name|Snapshot
argument_list|>
block|{
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|method|SizeMatcher
specifier|public
name|SizeMatcher
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|matchesSafely
specifier|public
name|boolean
name|matchesSafely
parameter_list|(
name|Translog
operator|.
name|Snapshot
name|snapshot
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
try|try
block|{
while|while
condition|(
name|snapshot
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"failed to advance snapshot"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
return|return
name|size
operator|==
name|count
return|;
block|}
annotation|@
name|Override
DECL|method|describeTo
specifier|public
name|void
name|describeTo
parameter_list|(
name|Description
name|description
parameter_list|)
block|{
name|description
operator|.
name|appendText
argument_list|(
literal|"a snapshot with size "
argument_list|)
operator|.
name|appendValue
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|EqualMatcher
specifier|public
specifier|static
class|class
name|EqualMatcher
extends|extends
name|TypeSafeMatcher
argument_list|<
name|Translog
operator|.
name|Snapshot
argument_list|>
block|{
DECL|field|expectedOps
specifier|private
specifier|final
name|Translog
operator|.
name|Operation
index|[]
name|expectedOps
decl_stmt|;
DECL|field|failureMsg
name|String
name|failureMsg
init|=
literal|null
decl_stmt|;
DECL|method|EqualMatcher
specifier|public
name|EqualMatcher
parameter_list|(
name|Translog
operator|.
name|Operation
index|[]
name|expectedOps
parameter_list|)
block|{
name|this
operator|.
name|expectedOps
operator|=
name|expectedOps
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|matchesSafely
specifier|protected
name|boolean
name|matchesSafely
parameter_list|(
name|Translog
operator|.
name|Snapshot
name|snapshot
parameter_list|)
block|{
try|try
block|{
name|Translog
operator|.
name|Operation
name|op
decl_stmt|;
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
operator|,
name|op
operator|=
name|snapshot
operator|.
name|next
argument_list|()
init|;
name|op
operator|!=
literal|null
operator|&&
name|i
operator|<
name|expectedOps
operator|.
name|length
condition|;
name|i
operator|++
operator|,
name|op
operator|=
name|snapshot
operator|.
name|next
argument_list|()
control|)
block|{
if|if
condition|(
name|expectedOps
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|op
argument_list|)
operator|==
literal|false
condition|)
block|{
name|failureMsg
operator|=
literal|"position ["
operator|+
name|i
operator|+
literal|"] expected ["
operator|+
name|expectedOps
index|[
name|i
index|]
operator|+
literal|"] but found ["
operator|+
name|op
operator|+
literal|"]"
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|i
operator|<
name|expectedOps
operator|.
name|length
condition|)
block|{
name|failureMsg
operator|=
literal|"expected ["
operator|+
name|expectedOps
operator|.
name|length
operator|+
literal|"] ops but only found ["
operator|+
name|i
operator|+
literal|"]"
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|op
operator|!=
literal|null
condition|)
block|{
name|int
name|count
init|=
literal|1
decl_stmt|;
comment|// to account for the op we already read
while|while
condition|(
name|snapshot
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
name|failureMsg
operator|=
literal|"expected ["
operator|+
name|expectedOps
operator|.
name|length
operator|+
literal|"] ops but got ["
operator|+
operator|(
name|expectedOps
operator|.
name|length
operator|+
name|count
operator|)
operator|+
literal|"]"
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"failed to read snapshot content"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|describeTo
specifier|public
name|void
name|describeTo
parameter_list|(
name|Description
name|description
parameter_list|)
block|{
name|description
operator|.
name|appendText
argument_list|(
name|failureMsg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

