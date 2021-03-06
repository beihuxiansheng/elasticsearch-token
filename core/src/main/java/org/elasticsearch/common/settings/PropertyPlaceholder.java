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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
comment|/**  * Utility class for working with Strings that have placeholder values in them. A placeholder takes the form  *<tt>${name}</tt>. Using<tt>PropertyPlaceholder</tt> these placeholders can be substituted for  * user-supplied values.  *<p>  * Values for substitution can be supplied using a {@link Properties} instance or using a  * {@link PlaceholderResolver}.  */
end_comment

begin_class
DECL|class|PropertyPlaceholder
class|class
name|PropertyPlaceholder
block|{
DECL|field|placeholderPrefix
specifier|private
specifier|final
name|String
name|placeholderPrefix
decl_stmt|;
DECL|field|placeholderSuffix
specifier|private
specifier|final
name|String
name|placeholderSuffix
decl_stmt|;
DECL|field|ignoreUnresolvablePlaceholders
specifier|private
specifier|final
name|boolean
name|ignoreUnresolvablePlaceholders
decl_stmt|;
comment|/**      * Creates a new<code>PropertyPlaceholderHelper</code> that uses the supplied prefix and suffix.      *      * @param placeholderPrefix              the prefix that denotes the start of a placeholder.      * @param placeholderSuffix              the suffix that denotes the end of a placeholder.      * @param ignoreUnresolvablePlaceholders indicates whether unresolvable placeholders should be ignored      *                                       (<code>true</code>) or cause an exception (<code>false</code>).      */
DECL|method|PropertyPlaceholder
name|PropertyPlaceholder
parameter_list|(
name|String
name|placeholderPrefix
parameter_list|,
name|String
name|placeholderSuffix
parameter_list|,
name|boolean
name|ignoreUnresolvablePlaceholders
parameter_list|)
block|{
name|this
operator|.
name|placeholderPrefix
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|placeholderPrefix
argument_list|)
expr_stmt|;
name|this
operator|.
name|placeholderSuffix
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|placeholderSuffix
argument_list|)
expr_stmt|;
name|this
operator|.
name|ignoreUnresolvablePlaceholders
operator|=
name|ignoreUnresolvablePlaceholders
expr_stmt|;
block|}
comment|/**      * Replaces all placeholders of format<code>${name}</code> with the value returned from the supplied {@link      * PlaceholderResolver}.      *      * @param value               the value containing the placeholders to be replaced.      * @param placeholderResolver the<code>PlaceholderResolver</code> to use for replacement.      * @return the supplied value with placeholders replaced inline.      * @throws NullPointerException if value is null      */
DECL|method|replacePlaceholders
name|String
name|replacePlaceholders
parameter_list|(
name|String
name|value
parameter_list|,
name|PlaceholderResolver
name|placeholderResolver
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|parseStringValue
argument_list|(
name|value
argument_list|,
name|placeholderResolver
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
return|;
block|}
DECL|method|parseStringValue
specifier|private
name|String
name|parseStringValue
parameter_list|(
name|String
name|strVal
parameter_list|,
name|PlaceholderResolver
name|placeholderResolver
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|visitedPlaceholders
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
name|strVal
argument_list|)
decl_stmt|;
name|int
name|startIndex
init|=
name|strVal
operator|.
name|indexOf
argument_list|(
name|this
operator|.
name|placeholderPrefix
argument_list|)
decl_stmt|;
while|while
condition|(
name|startIndex
operator|!=
operator|-
literal|1
condition|)
block|{
name|int
name|endIndex
init|=
name|findPlaceholderEndIndex
argument_list|(
name|buf
argument_list|,
name|startIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|endIndex
operator|!=
operator|-
literal|1
condition|)
block|{
name|String
name|placeholder
init|=
name|buf
operator|.
name|substring
argument_list|(
name|startIndex
operator|+
name|this
operator|.
name|placeholderPrefix
operator|.
name|length
argument_list|()
argument_list|,
name|endIndex
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|visitedPlaceholders
operator|.
name|add
argument_list|(
name|placeholder
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Circular placeholder reference '"
operator|+
name|placeholder
operator|+
literal|"' in property definitions"
argument_list|)
throw|;
block|}
comment|// Recursive invocation, parsing placeholders contained in the placeholder key.
name|placeholder
operator|=
name|parseStringValue
argument_list|(
name|placeholder
argument_list|,
name|placeholderResolver
argument_list|,
name|visitedPlaceholders
argument_list|)
expr_stmt|;
comment|// Now obtain the value for the fully resolved key...
name|int
name|defaultValueIdx
init|=
name|placeholder
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|String
name|defaultValue
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|defaultValueIdx
operator|!=
operator|-
literal|1
condition|)
block|{
name|defaultValue
operator|=
name|placeholder
operator|.
name|substring
argument_list|(
name|defaultValueIdx
operator|+
literal|1
argument_list|)
expr_stmt|;
name|placeholder
operator|=
name|placeholder
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|defaultValueIdx
argument_list|)
expr_stmt|;
block|}
name|String
name|propVal
init|=
name|placeholderResolver
operator|.
name|resolvePlaceholder
argument_list|(
name|placeholder
argument_list|)
decl_stmt|;
if|if
condition|(
name|propVal
operator|==
literal|null
condition|)
block|{
name|propVal
operator|=
name|defaultValue
expr_stmt|;
block|}
if|if
condition|(
name|propVal
operator|==
literal|null
operator|&&
name|placeholderResolver
operator|.
name|shouldIgnoreMissing
argument_list|(
name|placeholder
argument_list|)
condition|)
block|{
if|if
condition|(
name|placeholderResolver
operator|.
name|shouldRemoveMissingPlaceholder
argument_list|(
name|placeholder
argument_list|)
condition|)
block|{
name|propVal
operator|=
literal|""
expr_stmt|;
block|}
else|else
block|{
return|return
name|strVal
return|;
block|}
block|}
if|if
condition|(
name|propVal
operator|!=
literal|null
condition|)
block|{
comment|// Recursive invocation, parsing placeholders contained in the
comment|// previously resolved placeholder value.
name|propVal
operator|=
name|parseStringValue
argument_list|(
name|propVal
argument_list|,
name|placeholderResolver
argument_list|,
name|visitedPlaceholders
argument_list|)
expr_stmt|;
name|buf
operator|.
name|replace
argument_list|(
name|startIndex
argument_list|,
name|endIndex
operator|+
name|this
operator|.
name|placeholderSuffix
operator|.
name|length
argument_list|()
argument_list|,
name|propVal
argument_list|)
expr_stmt|;
name|startIndex
operator|=
name|buf
operator|.
name|indexOf
argument_list|(
name|this
operator|.
name|placeholderPrefix
argument_list|,
name|startIndex
operator|+
name|propVal
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|ignoreUnresolvablePlaceholders
condition|)
block|{
comment|// Proceed with unprocessed value.
name|startIndex
operator|=
name|buf
operator|.
name|indexOf
argument_list|(
name|this
operator|.
name|placeholderPrefix
argument_list|,
name|endIndex
operator|+
name|this
operator|.
name|placeholderSuffix
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Could not resolve placeholder '"
operator|+
name|placeholder
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|visitedPlaceholders
operator|.
name|remove
argument_list|(
name|placeholder
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|startIndex
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|findPlaceholderEndIndex
specifier|private
name|int
name|findPlaceholderEndIndex
parameter_list|(
name|CharSequence
name|buf
parameter_list|,
name|int
name|startIndex
parameter_list|)
block|{
name|int
name|index
init|=
name|startIndex
operator|+
name|this
operator|.
name|placeholderPrefix
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|withinNestedPlaceholder
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|index
operator|<
name|buf
operator|.
name|length
argument_list|()
condition|)
block|{
if|if
condition|(
name|Strings
operator|.
name|substringMatch
argument_list|(
name|buf
argument_list|,
name|index
argument_list|,
name|this
operator|.
name|placeholderSuffix
argument_list|)
condition|)
block|{
if|if
condition|(
name|withinNestedPlaceholder
operator|>
literal|0
condition|)
block|{
name|withinNestedPlaceholder
operator|--
expr_stmt|;
name|index
operator|=
name|index
operator|+
name|this
operator|.
name|placeholderSuffix
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return
name|index
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|Strings
operator|.
name|substringMatch
argument_list|(
name|buf
argument_list|,
name|index
argument_list|,
name|this
operator|.
name|placeholderPrefix
argument_list|)
condition|)
block|{
name|withinNestedPlaceholder
operator|++
expr_stmt|;
name|index
operator|=
name|index
operator|+
name|this
operator|.
name|placeholderPrefix
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|index
operator|++
expr_stmt|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/**      * Strategy interface used to resolve replacement values for placeholders contained in Strings.      *      * @see PropertyPlaceholder      */
DECL|interface|PlaceholderResolver
interface|interface
name|PlaceholderResolver
block|{
comment|/**          * Resolves the supplied placeholder name into the replacement value.          *          * @param placeholderName the name of the placeholder to resolve.          * @return the replacement value or<code>null</code> if no replacement is to be made.          */
DECL|method|resolvePlaceholder
name|String
name|resolvePlaceholder
parameter_list|(
name|String
name|placeholderName
parameter_list|)
function_decl|;
DECL|method|shouldIgnoreMissing
name|boolean
name|shouldIgnoreMissing
parameter_list|(
name|String
name|placeholderName
parameter_list|)
function_decl|;
comment|/**          * Allows for special handling for ignored missing placeholders that may be resolved elsewhere          *          * @param placeholderName the name of the placeholder to resolve.          * @return true if the placeholder should be replaced with a empty string          */
DECL|method|shouldRemoveMissingPlaceholder
name|boolean
name|shouldRemoveMissingPlaceholder
parameter_list|(
name|String
name|placeholderName
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

