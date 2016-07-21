begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.functionscore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|functionscore
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
name|ParseField
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
name|ParsingException
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
name|bytes
operator|.
name|BytesReference
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
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
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
name|XContentParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryParseContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|SearchPlugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|MultiValueMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchModule
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
name|function
operator|.
name|BiFunction
import|;
end_import

begin_comment
comment|/**  * Parser used for all decay functions, one instance each. It parses this kind  * of input:  *  *<pre>  *<code>  * {  *      "fieldname1" : {  *          "origin" = "someValue",  *          "scale" = "someValue"  *      },  *      "multi_value_mode" : "min"  * }  *</code>  *</pre>  *  * "origin" here refers to the reference point and "scale" to the level of  * uncertainty you have in your origin.  *<p>  *  * For example, you might want to retrieve an event that took place around the  * 20 May 2010 somewhere near Berlin. You are mainly interested in events that  * are close to the 20 May 2010 but you are unsure about your guess, maybe it  * was a week before or after that. Your "origin" for the date field would be  * "20 May 2010" and your "scale" would be "7d".  *  *<p>  * This class parses the input and creates a scoring function from the  * parameters origin and scale.  *<p>  * To write a new decay scoring function, create a new class that extends  * {@link DecayFunctionBuilder}, setup a PARSER field with this class, and  * register them in {@link SearchModule#registerScoreFunctions} or {@link SearchPlugin#getScoreFunctions}  * See {@link GaussDecayFunctionBuilder#PARSER} for an example.  */
end_comment

begin_class
DECL|class|DecayFunctionParser
specifier|public
specifier|final
class|class
name|DecayFunctionParser
parameter_list|<
name|DFB
extends|extends
name|DecayFunctionBuilder
parameter_list|<
name|DFB
parameter_list|>
parameter_list|>
implements|implements
name|ScoreFunctionParser
argument_list|<
name|DFB
argument_list|>
block|{
DECL|field|MULTI_VALUE_MODE
specifier|public
specifier|static
specifier|final
name|ParseField
name|MULTI_VALUE_MODE
init|=
operator|new
name|ParseField
argument_list|(
literal|"multi_value_mode"
argument_list|)
decl_stmt|;
DECL|field|createFromBytes
specifier|private
specifier|final
name|BiFunction
argument_list|<
name|String
argument_list|,
name|BytesReference
argument_list|,
name|DFB
argument_list|>
name|createFromBytes
decl_stmt|;
comment|/**      * Create the parser using a method reference to a "create from bytes" constructor for the {@linkplain DecayFunctionBuilder}. We use a      * method reference here so each use of this class doesn't have to subclass it.      */
DECL|method|DecayFunctionParser
specifier|public
name|DecayFunctionParser
parameter_list|(
name|BiFunction
argument_list|<
name|String
argument_list|,
name|BytesReference
argument_list|,
name|DFB
argument_list|>
name|createFromBytes
parameter_list|)
block|{
name|this
operator|.
name|createFromBytes
operator|=
name|createFromBytes
expr_stmt|;
block|}
comment|/**      * Parses bodies of the kind      *      *<pre>      *<code>      * {      *      "fieldname1" : {      *          "origin" : "someValue",      *          "scale" : "someValue"      *      },      *      "multi_value_mode" : "min"      * }      *</code>      *</pre>      */
annotation|@
name|Override
DECL|method|fromXContent
specifier|public
name|DFB
name|fromXContent
parameter_list|(
name|QueryParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParsingException
block|{
name|XContentParser
name|parser
init|=
name|context
operator|.
name|parser
argument_list|()
decl_stmt|;
name|String
name|currentFieldName
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|MultiValueMode
name|multiValueMode
init|=
name|DecayFunctionBuilder
operator|.
name|DEFAULT_MULTI_VALUE_MODE
decl_stmt|;
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
name|BytesReference
name|functionBytes
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|fieldName
operator|=
name|currentFieldName
expr_stmt|;
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|copyCurrentStructure
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|functionBytes
operator|=
name|builder
operator|.
name|bytes
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|context
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|MULTI_VALUE_MODE
argument_list|)
condition|)
block|{
name|multiValueMode
operator|=
name|MultiValueMode
operator|.
name|fromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"malformed score function score parameters."
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|fieldName
operator|==
literal|null
operator|||
name|functionBytes
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"malformed score function score parameters."
argument_list|)
throw|;
block|}
name|DFB
name|functionBuilder
init|=
name|createFromBytes
operator|.
name|apply
argument_list|(
name|fieldName
argument_list|,
name|functionBytes
argument_list|)
decl_stmt|;
name|functionBuilder
operator|.
name|setMultiValueMode
argument_list|(
name|multiValueMode
argument_list|)
expr_stmt|;
return|return
name|functionBuilder
return|;
block|}
block|}
end_class

end_unit

