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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Matches blacklist patterns.  *  * Currently the following syntax is supported:  *  *<ul>  *<li>Exact matches, as in<code>cat.aliases/10_basic/Empty cluster</code></li>  *<li>Wildcard matches within the same segment of a path , as in<code>indices.get/10_basic/*allow_no_indices*</code>. This will  *  match<code>indices.get/10_basic/allow_no_indices</code>,<code>indices.get/10_basic/allow_no_indices_at_all</code> but not  *<code>indices.get/10_basic/advanced/allow_no_indices</code> (contains an additional segment)</li>  *</ul>  *  * Each blacklist pattern is a suffix match on the path. Empty patterns are not allowed.  */
end_comment

begin_class
DECL|class|BlacklistedPathPatternMatcher
specifier|final
class|class
name|BlacklistedPathPatternMatcher
block|{
DECL|field|pattern
specifier|private
specifier|final
name|Pattern
name|pattern
decl_stmt|;
comment|/**      * Constructs a new<code>BlacklistedPathPatternMatcher</code> instance from the provided suffix pattern.      *      * @param p The suffix pattern. Must be a non-empty string.      */
DECL|method|BlacklistedPathPatternMatcher
name|BlacklistedPathPatternMatcher
parameter_list|(
name|String
name|p
parameter_list|)
block|{
comment|// guard against accidentally matching everything as an empty string lead to the pattern ".*" which matches everything
if|if
condition|(
name|p
operator|==
literal|null
operator|||
name|p
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Empty blacklist patterns are not supported"
argument_list|)
throw|;
block|}
comment|// very simple transformation from wildcard to a proper regex
name|String
name|finalPattern
init|=
name|p
operator|.
name|replaceAll
argument_list|(
literal|"\\*"
argument_list|,
literal|"[^/]*"
argument_list|)
comment|// support wildcard matches (within a single path segment)
operator|.
name|replaceAll
argument_list|(
literal|"\\\\,"
argument_list|,
literal|","
argument_list|)
decl_stmt|;
comment|// restore previously escaped ',' in paths.
comment|// suffix match
name|pattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*"
operator|+
name|finalPattern
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks whether the provided path matches the suffix pattern, i.e. "/foo/bar" will match the pattern "bar".      *      * @param path The path to match. Must not be null.      * @return true iff this path is a suffix match.      */
DECL|method|isSuffixMatch
specifier|public
name|boolean
name|isSuffixMatch
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|pattern
operator|.
name|matcher
argument_list|(
name|path
argument_list|)
operator|.
name|matches
argument_list|()
return|;
block|}
block|}
end_class

end_unit

