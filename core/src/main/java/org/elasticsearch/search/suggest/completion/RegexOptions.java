begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.completion
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion
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
name|automaton
operator|.
name|Operations
import|;
end_import

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
name|automaton
operator|.
name|RegExp
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|io
operator|.
name|stream
operator|.
name|Writeable
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
name|index
operator|.
name|query
operator|.
name|RegexpFlag
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

begin_comment
comment|/**  * Regular expression options for completion suggester  */
end_comment

begin_class
DECL|class|RegexOptions
specifier|public
class|class
name|RegexOptions
implements|implements
name|ToXContent
implements|,
name|Writeable
argument_list|<
name|RegexOptions
argument_list|>
block|{
DECL|field|NAME
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"regex"
decl_stmt|;
DECL|field|REGEX_OPTIONS
specifier|static
specifier|final
name|ParseField
name|REGEX_OPTIONS
init|=
operator|new
name|ParseField
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
DECL|field|FLAGS_VALUE
specifier|static
specifier|final
name|ParseField
name|FLAGS_VALUE
init|=
operator|new
name|ParseField
argument_list|(
literal|"flags"
argument_list|,
literal|"flags_value"
argument_list|)
decl_stmt|;
DECL|field|MAX_DETERMINIZED_STATES
specifier|static
specifier|final
name|ParseField
name|MAX_DETERMINIZED_STATES
init|=
operator|new
name|ParseField
argument_list|(
literal|"max_determinized_states"
argument_list|)
decl_stmt|;
DECL|field|flagsValue
specifier|private
name|int
name|flagsValue
decl_stmt|;
DECL|field|maxDeterminizedStates
specifier|private
name|int
name|maxDeterminizedStates
decl_stmt|;
DECL|method|RegexOptions
specifier|private
name|RegexOptions
parameter_list|()
block|{     }
DECL|method|RegexOptions
specifier|private
name|RegexOptions
parameter_list|(
name|int
name|flagsValue
parameter_list|,
name|int
name|maxDeterminizedStates
parameter_list|)
block|{
name|this
operator|.
name|flagsValue
operator|=
name|flagsValue
expr_stmt|;
name|this
operator|.
name|maxDeterminizedStates
operator|=
name|maxDeterminizedStates
expr_stmt|;
block|}
comment|/**      * Returns internal regular expression syntax flag value      * see {@link RegexpFlag#value()}      */
DECL|method|getFlagsValue
specifier|public
name|int
name|getFlagsValue
parameter_list|()
block|{
return|return
name|flagsValue
return|;
block|}
comment|/**      * Returns the maximum automaton states allowed for fuzzy expansion      */
DECL|method|getMaxDeterminizedStates
specifier|public
name|int
name|getMaxDeterminizedStates
parameter_list|()
block|{
return|return
name|maxDeterminizedStates
return|;
block|}
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|RegexOptions
name|that
init|=
operator|(
name|RegexOptions
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|flagsValue
operator|!=
name|that
operator|.
name|flagsValue
condition|)
return|return
literal|false
return|;
return|return
name|maxDeterminizedStates
operator|==
name|that
operator|.
name|maxDeterminizedStates
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|flagsValue
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|maxDeterminizedStates
expr_stmt|;
return|return
name|result
return|;
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
name|startObject
argument_list|(
name|REGEX_OPTIONS
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|FLAGS_VALUE
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|flagsValue
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|MAX_DETERMINIZED_STATES
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|maxDeterminizedStates
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|readRegexOptions
specifier|public
specifier|static
name|RegexOptions
name|readRegexOptions
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|RegexOptions
name|regexOptions
init|=
operator|new
name|RegexOptions
argument_list|()
decl_stmt|;
name|regexOptions
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|regexOptions
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|RegexOptions
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|flagsValue
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxDeterminizedStates
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|flagsValue
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|maxDeterminizedStates
argument_list|)
expr_stmt|;
block|}
comment|/**      * Options for regular expression queries      */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|flagsValue
specifier|private
name|int
name|flagsValue
init|=
name|RegExp
operator|.
name|ALL
decl_stmt|;
DECL|field|maxDeterminizedStates
specifier|private
name|int
name|maxDeterminizedStates
init|=
name|Operations
operator|.
name|DEFAULT_MAX_DETERMINIZED_STATES
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|()
block|{         }
comment|/**          * Sets the regular expression syntax flags          * see {@link RegexpFlag}          */
DECL|method|setFlags
specifier|public
name|Builder
name|setFlags
parameter_list|(
name|String
name|flags
parameter_list|)
block|{
name|this
operator|.
name|flagsValue
operator|=
name|RegexpFlag
operator|.
name|resolveValue
argument_list|(
name|flags
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Sets the maximum automaton states allowed for the regular expression expansion          */
DECL|method|setMaxDeterminizedStates
specifier|public
name|Builder
name|setMaxDeterminizedStates
parameter_list|(
name|int
name|maxDeterminizedStates
parameter_list|)
block|{
if|if
condition|(
name|maxDeterminizedStates
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxDeterminizedStates must not be negative"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxDeterminizedStates
operator|=
name|maxDeterminizedStates
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|RegexOptions
name|build
parameter_list|()
block|{
return|return
operator|new
name|RegexOptions
argument_list|(
name|flagsValue
argument_list|,
name|maxDeterminizedStates
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

