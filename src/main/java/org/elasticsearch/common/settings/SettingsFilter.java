begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.settings
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
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
name|component
operator|.
name|AbstractComponent
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
name|inject
operator|.
name|Inject
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
name|regex
operator|.
name|Regex
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
operator|.
name|Params
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SettingsFilter
specifier|public
class|class
name|SettingsFilter
extends|extends
name|AbstractComponent
block|{
comment|/**      * Can be used to specify settings filter that will be used to filter out matching settings in toXContent method      */
DECL|field|SETTINGS_FILTER_PARAM
specifier|public
specifier|static
name|String
name|SETTINGS_FILTER_PARAM
init|=
literal|"settings_filter"
decl_stmt|;
DECL|field|patterns
specifier|private
specifier|final
name|CopyOnWriteArrayList
argument_list|<
name|String
argument_list|>
name|patterns
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|SettingsFilter
specifier|public
name|SettingsFilter
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds a new simple pattern to the list of filters      *      * @param pattern      */
DECL|method|addFilter
specifier|public
name|void
name|addFilter
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|patterns
operator|.
name|add
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
block|}
comment|/**      * Removes a simple pattern from the list of filters      *      * @param pattern      */
DECL|method|removeFilter
specifier|public
name|void
name|removeFilter
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|patterns
operator|.
name|remove
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
block|}
DECL|method|getPatterns
specifier|public
name|String
name|getPatterns
parameter_list|()
block|{
return|return
name|Strings
operator|.
name|collectionToDelimitedString
argument_list|(
name|patterns
argument_list|,
literal|","
argument_list|)
return|;
block|}
DECL|method|addFilterSettingParams
specifier|public
name|void
name|addFilterSettingParams
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
if|if
condition|(
name|patterns
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|request
operator|.
name|params
argument_list|()
operator|.
name|put
argument_list|(
name|SETTINGS_FILTER_PARAM
argument_list|,
name|getPatterns
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|filterSettings
specifier|public
specifier|static
name|Settings
name|filterSettings
parameter_list|(
name|Params
name|params
parameter_list|,
name|Settings
name|settings
parameter_list|)
block|{
name|String
name|patterns
init|=
name|params
operator|.
name|param
argument_list|(
name|SETTINGS_FILTER_PARAM
argument_list|)
decl_stmt|;
name|Settings
name|filteredSettings
init|=
name|settings
decl_stmt|;
if|if
condition|(
name|patterns
operator|!=
literal|null
operator|&&
name|patterns
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|filteredSettings
operator|=
name|SettingsFilter
operator|.
name|filterSettings
argument_list|(
name|patterns
argument_list|,
name|filteredSettings
argument_list|)
expr_stmt|;
block|}
return|return
name|filteredSettings
return|;
block|}
DECL|method|filterSettings
specifier|public
specifier|static
name|Settings
name|filterSettings
parameter_list|(
name|String
name|patterns
parameter_list|,
name|Settings
name|settings
parameter_list|)
block|{
name|String
index|[]
name|patternArray
init|=
name|Strings
operator|.
name|delimitedListToStringArray
argument_list|(
name|patterns
argument_list|,
literal|","
argument_list|)
decl_stmt|;
name|ImmutableSettings
operator|.
name|Builder
name|builder
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|simpleMatchPatternList
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|pattern
range|:
name|patternArray
control|)
block|{
if|if
condition|(
name|Regex
operator|.
name|isSimpleMatchPattern
argument_list|(
name|pattern
argument_list|)
condition|)
block|{
name|simpleMatchPatternList
operator|.
name|add
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|remove
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|simpleMatchPatternList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
index|[]
name|simpleMatchPatterns
init|=
name|simpleMatchPatternList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|simpleMatchPatternList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|iterator
init|=
name|builder
operator|.
name|internalMap
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|current
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|simpleMatchPatterns
argument_list|,
name|current
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

