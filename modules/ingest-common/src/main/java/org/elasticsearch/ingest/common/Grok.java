begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|common
package|;
end_package

begin_import
import|import
name|org
operator|.
name|jcodings
operator|.
name|specific
operator|.
name|UTF8Encoding
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joni
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joni
operator|.
name|NameEntry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joni
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joni
operator|.
name|Regex
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joni
operator|.
name|Region
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joni
operator|.
name|Syntax
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joni
operator|.
name|exception
operator|.
name|ValueException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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

begin_class
DECL|class|Grok
specifier|final
class|class
name|Grok
block|{
DECL|field|NAME_GROUP
specifier|private
specifier|static
specifier|final
name|String
name|NAME_GROUP
init|=
literal|"name"
decl_stmt|;
DECL|field|SUBNAME_GROUP
specifier|private
specifier|static
specifier|final
name|String
name|SUBNAME_GROUP
init|=
literal|"subname"
decl_stmt|;
DECL|field|PATTERN_GROUP
specifier|private
specifier|static
specifier|final
name|String
name|PATTERN_GROUP
init|=
literal|"pattern"
decl_stmt|;
DECL|field|DEFINITION_GROUP
specifier|private
specifier|static
specifier|final
name|String
name|DEFINITION_GROUP
init|=
literal|"definition"
decl_stmt|;
DECL|field|GROK_PATTERN
specifier|private
specifier|static
specifier|final
name|String
name|GROK_PATTERN
init|=
literal|"%\\{"
operator|+
literal|"(?<name>"
operator|+
literal|"(?<pattern>[A-z0-9]+)"
operator|+
literal|"(?::(?<subname>[A-z0-9_:.-]+))?"
operator|+
literal|")"
operator|+
literal|"(?:=(?<definition>"
operator|+
literal|"(?:"
operator|+
literal|"(?:[^{}]+|\\.+)+"
operator|+
literal|")+"
operator|+
literal|")"
operator|+
literal|")?"
operator|+
literal|"\\}"
decl_stmt|;
DECL|field|GROK_PATTERN_REGEX
specifier|private
specifier|static
specifier|final
name|Regex
name|GROK_PATTERN_REGEX
init|=
operator|new
name|Regex
argument_list|(
name|GROK_PATTERN
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
name|GROK_PATTERN
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
operator|.
name|length
argument_list|,
name|Option
operator|.
name|NONE
argument_list|,
name|UTF8Encoding
operator|.
name|INSTANCE
argument_list|,
name|Syntax
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
DECL|field|patternBank
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|patternBank
decl_stmt|;
DECL|field|namedCaptures
specifier|private
specifier|final
name|boolean
name|namedCaptures
decl_stmt|;
DECL|field|compiledExpression
specifier|private
specifier|final
name|Regex
name|compiledExpression
decl_stmt|;
DECL|field|expression
specifier|private
specifier|final
name|String
name|expression
decl_stmt|;
DECL|method|Grok
name|Grok
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|patternBank
parameter_list|,
name|String
name|grokPattern
parameter_list|)
block|{
name|this
argument_list|(
name|patternBank
argument_list|,
name|grokPattern
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|Grok
name|Grok
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|patternBank
parameter_list|,
name|String
name|grokPattern
parameter_list|,
name|boolean
name|namedCaptures
parameter_list|)
block|{
name|this
operator|.
name|patternBank
operator|=
name|patternBank
expr_stmt|;
name|this
operator|.
name|namedCaptures
operator|=
name|namedCaptures
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|toRegex
argument_list|(
name|grokPattern
argument_list|)
expr_stmt|;
name|byte
index|[]
name|expressionBytes
init|=
name|expression
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|this
operator|.
name|compiledExpression
operator|=
operator|new
name|Regex
argument_list|(
name|expressionBytes
argument_list|,
literal|0
argument_list|,
name|expressionBytes
operator|.
name|length
argument_list|,
name|Option
operator|.
name|DEFAULT
argument_list|,
name|UTF8Encoding
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
DECL|method|groupMatch
specifier|public
name|String
name|groupMatch
parameter_list|(
name|String
name|name
parameter_list|,
name|Region
name|region
parameter_list|,
name|String
name|pattern
parameter_list|)
block|{
try|try
block|{
name|int
name|number
init|=
name|GROK_PATTERN_REGEX
operator|.
name|nameToBackrefNumber
argument_list|(
name|name
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
name|name
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
operator|.
name|length
argument_list|,
name|region
argument_list|)
decl_stmt|;
name|int
name|begin
init|=
name|region
operator|.
name|beg
index|[
name|number
index|]
decl_stmt|;
name|int
name|end
init|=
name|region
operator|.
name|end
index|[
name|number
index|]
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|pattern
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
name|begin
argument_list|,
name|end
operator|-
name|begin
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|StringIndexOutOfBoundsException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|ValueException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * converts a grok expression into a named regex expression      *      * @return named regex expression      */
DECL|method|toRegex
specifier|public
name|String
name|toRegex
parameter_list|(
name|String
name|grokPattern
parameter_list|)
block|{
name|byte
index|[]
name|grokPatternBytes
init|=
name|grokPattern
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|Matcher
name|matcher
init|=
name|GROK_PATTERN_REGEX
operator|.
name|matcher
argument_list|(
name|grokPatternBytes
argument_list|)
decl_stmt|;
name|int
name|result
init|=
name|matcher
operator|.
name|search
argument_list|(
literal|0
argument_list|,
name|grokPatternBytes
operator|.
name|length
argument_list|,
name|Option
operator|.
name|NONE
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
operator|-
literal|1
condition|)
block|{
name|Region
name|region
init|=
name|matcher
operator|.
name|getEagerRegion
argument_list|()
decl_stmt|;
name|String
name|namedPatternRef
init|=
name|groupMatch
argument_list|(
name|NAME_GROUP
argument_list|,
name|region
argument_list|,
name|grokPattern
argument_list|)
decl_stmt|;
name|String
name|subName
init|=
name|groupMatch
argument_list|(
name|SUBNAME_GROUP
argument_list|,
name|region
argument_list|,
name|grokPattern
argument_list|)
decl_stmt|;
comment|// TODO(tal): Support definitions
name|String
name|definition
init|=
name|groupMatch
argument_list|(
name|DEFINITION_GROUP
argument_list|,
name|region
argument_list|,
name|grokPattern
argument_list|)
decl_stmt|;
name|String
name|patternName
init|=
name|groupMatch
argument_list|(
name|PATTERN_GROUP
argument_list|,
name|region
argument_list|,
name|grokPattern
argument_list|)
decl_stmt|;
name|String
name|pattern
init|=
name|patternBank
operator|.
name|get
argument_list|(
name|patternName
argument_list|)
decl_stmt|;
if|if
condition|(
name|pattern
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to find pattern ["
operator|+
name|patternName
operator|+
literal|"] in Grok's pattern dictionary"
argument_list|)
throw|;
block|}
name|String
name|grokPart
decl_stmt|;
if|if
condition|(
name|namedCaptures
operator|&&
name|subName
operator|!=
literal|null
condition|)
block|{
name|grokPart
operator|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|US
argument_list|,
literal|"(?<%s>%s)"
argument_list|,
name|namedPatternRef
argument_list|,
name|pattern
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|namedCaptures
condition|)
block|{
name|grokPart
operator|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|US
argument_list|,
literal|"(?<%s>%s)"
argument_list|,
name|patternName
operator|+
literal|"_"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|result
argument_list|)
argument_list|,
name|pattern
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|grokPart
operator|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|US
argument_list|,
literal|"(?:%s)"
argument_list|,
name|pattern
argument_list|)
expr_stmt|;
block|}
name|String
name|start
init|=
operator|new
name|String
argument_list|(
name|grokPatternBytes
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|String
name|rest
init|=
operator|new
name|String
argument_list|(
name|grokPatternBytes
argument_list|,
name|region
operator|.
name|end
index|[
literal|0
index|]
argument_list|,
name|grokPatternBytes
operator|.
name|length
operator|-
name|region
operator|.
name|end
index|[
literal|0
index|]
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
return|return
name|start
operator|+
name|toRegex
argument_list|(
name|grokPart
operator|+
name|rest
argument_list|)
return|;
block|}
return|return
name|grokPattern
return|;
block|}
comment|/**      * Checks whether a specific text matches the defined grok expression.      *      * @param text the string to match      * @return true if grok expression matches text, false otherwise.      */
DECL|method|match
specifier|public
name|boolean
name|match
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
name|compiledExpression
operator|.
name|matcher
argument_list|(
name|text
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|result
init|=
name|matcher
operator|.
name|search
argument_list|(
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|()
argument_list|,
name|Option
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
return|return
operator|(
name|result
operator|!=
operator|-
literal|1
operator|)
return|;
block|}
comment|/**      * Matches and returns any named captures within a compiled grok expression that matched      * within the provided text.      *      * @param text the text to match and extract values from.      * @return a map containing field names and their respective coerced values that matched.      */
DECL|method|captures
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|captures
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|byte
index|[]
name|textAsBytes
init|=
name|text
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fields
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Matcher
name|matcher
init|=
name|compiledExpression
operator|.
name|matcher
argument_list|(
name|textAsBytes
argument_list|)
decl_stmt|;
name|int
name|result
init|=
name|matcher
operator|.
name|search
argument_list|(
literal|0
argument_list|,
name|textAsBytes
operator|.
name|length
argument_list|,
name|Option
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
operator|-
literal|1
operator|&&
name|compiledExpression
operator|.
name|numberOfNames
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Region
name|region
init|=
name|matcher
operator|.
name|getEagerRegion
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|NameEntry
argument_list|>
name|entry
init|=
name|compiledExpression
operator|.
name|namedBackrefIterator
argument_list|()
init|;
name|entry
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|NameEntry
name|e
init|=
name|entry
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|groupName
init|=
operator|new
name|String
argument_list|(
name|e
operator|.
name|name
argument_list|,
name|e
operator|.
name|nameP
argument_list|,
name|e
operator|.
name|nameEnd
operator|-
name|e
operator|.
name|nameP
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|number
range|:
name|e
operator|.
name|getBackRefs
argument_list|()
control|)
block|{
if|if
condition|(
name|region
operator|.
name|beg
index|[
name|number
index|]
operator|>=
literal|0
condition|)
block|{
name|String
name|matchValue
init|=
operator|new
name|String
argument_list|(
name|textAsBytes
argument_list|,
name|region
operator|.
name|beg
index|[
name|number
index|]
argument_list|,
name|region
operator|.
name|end
index|[
name|number
index|]
operator|-
name|region
operator|.
name|beg
index|[
name|number
index|]
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|GrokMatchGroup
name|match
init|=
operator|new
name|GrokMatchGroup
argument_list|(
name|groupName
argument_list|,
name|matchValue
argument_list|)
decl_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|match
operator|.
name|getName
argument_list|()
argument_list|,
name|match
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|fields
return|;
block|}
elseif|else
if|if
condition|(
name|result
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
name|fields
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

