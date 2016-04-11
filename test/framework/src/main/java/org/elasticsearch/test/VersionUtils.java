begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/** Utilities for selecting versions in tests */
end_comment

begin_class
DECL|class|VersionUtils
specifier|public
class|class
name|VersionUtils
block|{
DECL|field|SORTED_VERSIONS
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|Version
argument_list|>
name|SORTED_VERSIONS
decl_stmt|;
static|static
block|{
name|Field
index|[]
name|declaredFields
init|=
name|Version
operator|.
name|class
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Integer
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|declaredFields
control|)
block|{
specifier|final
name|int
name|mod
init|=
name|field
operator|.
name|getModifiers
argument_list|()
decl_stmt|;
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|mod
argument_list|)
operator|&&
name|Modifier
operator|.
name|isFinal
argument_list|(
name|mod
argument_list|)
operator|&&
name|Modifier
operator|.
name|isPublic
argument_list|(
name|mod
argument_list|)
condition|)
block|{
if|if
condition|(
name|field
operator|.
name|getType
argument_list|()
operator|==
name|Version
operator|.
name|class
condition|)
block|{
try|try
block|{
name|Version
name|object
init|=
operator|(
name|Version
operator|)
name|field
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|ids
operator|.
name|add
argument_list|(
name|object
operator|.
name|id
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
name|List
argument_list|<
name|Integer
argument_list|>
name|idList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|ids
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|idList
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Version
argument_list|>
name|version
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Integer
name|integer
range|:
name|idList
control|)
block|{
name|version
operator|.
name|add
argument_list|(
name|Version
operator|.
name|fromId
argument_list|(
name|integer
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SORTED_VERSIONS
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
comment|/** Returns immutable list of all known versions. */
DECL|method|allVersions
specifier|public
specifier|static
name|List
argument_list|<
name|Version
argument_list|>
name|allVersions
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|SORTED_VERSIONS
argument_list|)
return|;
block|}
DECL|method|getPreviousVersion
specifier|public
specifier|static
name|Version
name|getPreviousVersion
parameter_list|(
name|Version
name|version
parameter_list|)
block|{
name|int
name|index
init|=
name|SORTED_VERSIONS
operator|.
name|indexOf
argument_list|(
name|version
argument_list|)
decl_stmt|;
assert|assert
name|index
operator|>
literal|0
assert|;
return|return
name|SORTED_VERSIONS
operator|.
name|get
argument_list|(
name|index
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** Returns the {@link Version} before the {@link Version#CURRENT} */
DECL|method|getPreviousVersion
specifier|public
specifier|static
name|Version
name|getPreviousVersion
parameter_list|()
block|{
name|Version
name|version
init|=
name|getPreviousVersion
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
assert|assert
name|version
operator|.
name|before
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
assert|;
return|return
name|version
return|;
block|}
comment|/** Returns the oldest {@link Version} */
DECL|method|getFirstVersion
specifier|public
specifier|static
name|Version
name|getFirstVersion
parameter_list|()
block|{
return|return
name|SORTED_VERSIONS
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/** Returns a random {@link Version} from all available versions. */
DECL|method|randomVersion
specifier|public
specifier|static
name|Version
name|randomVersion
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
return|return
name|SORTED_VERSIONS
operator|.
name|get
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|SORTED_VERSIONS
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a random {@link Version} between<code>minVersion</code> and<code>maxVersion</code> (inclusive). */
DECL|method|randomVersionBetween
specifier|public
specifier|static
name|Version
name|randomVersionBetween
parameter_list|(
name|Random
name|random
parameter_list|,
name|Version
name|minVersion
parameter_list|,
name|Version
name|maxVersion
parameter_list|)
block|{
name|int
name|minVersionIndex
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|minVersion
operator|!=
literal|null
condition|)
block|{
name|minVersionIndex
operator|=
name|SORTED_VERSIONS
operator|.
name|indexOf
argument_list|(
name|minVersion
argument_list|)
expr_stmt|;
block|}
name|int
name|maxVersionIndex
init|=
name|SORTED_VERSIONS
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|maxVersion
operator|!=
literal|null
condition|)
block|{
name|maxVersionIndex
operator|=
name|SORTED_VERSIONS
operator|.
name|indexOf
argument_list|(
name|maxVersion
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minVersionIndex
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minVersion ["
operator|+
name|minVersion
operator|+
literal|"] does not exist."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|maxVersionIndex
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxVersion ["
operator|+
name|maxVersion
operator|+
literal|"] does not exist."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|minVersionIndex
operator|>
name|maxVersionIndex
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxVersion ["
operator|+
name|maxVersion
operator|+
literal|"] cannot be less than minVersion ["
operator|+
name|minVersion
operator|+
literal|"]"
argument_list|)
throw|;
block|}
else|else
block|{
comment|// minVersionIndex is inclusive so need to add 1 to this index
name|int
name|range
init|=
name|maxVersionIndex
operator|+
literal|1
operator|-
name|minVersionIndex
decl_stmt|;
return|return
name|SORTED_VERSIONS
operator|.
name|get
argument_list|(
name|minVersionIndex
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|range
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|isSnapshot
specifier|public
specifier|static
name|boolean
name|isSnapshot
parameter_list|(
name|Version
name|version
parameter_list|)
block|{
if|if
condition|(
name|Version
operator|.
name|CURRENT
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

