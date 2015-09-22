begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.analysis
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|RuleBasedCollator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|util
operator|.
name|ULocale
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
name|analysis
operator|.
name|TokenStream
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
name|inject
operator|.
name|assistedinject
operator|.
name|Assisted
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
name|Streams
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
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|Environment
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
name|Index
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
name|settings
operator|.
name|IndexSettings
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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_comment
comment|/**  * An ICU based collation token filter. There are two ways to configure collation:  *<p>The first is simply specifying the locale (defaults to the default locale). The<tt>language</tt>  * parameter is the lowercase two-letter ISO-639 code. An additional<tt>country</tt> and<tt>variant</tt>  * can be provided.  *<p>The second option is to specify collation rules as defined in the<a href="http://www.icu-project.org/userguide/Collate_Customization.html">  * Collation customization</a> chapter in icu docs. The<tt>rules</tt> parameter can either embed the rules definition  * in the settings or refer to an external location (preferable located under the<tt>config</tt> location, relative to it).  */
end_comment

begin_class
DECL|class|IcuCollationTokenFilterFactory
specifier|public
class|class
name|IcuCollationTokenFilterFactory
extends|extends
name|AbstractTokenFilterFactory
block|{
DECL|field|collator
specifier|private
specifier|final
name|Collator
name|collator
decl_stmt|;
annotation|@
name|Inject
DECL|method|IcuCollationTokenFilterFactory
specifier|public
name|IcuCollationTokenFilterFactory
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|Environment
name|environment
parameter_list|,
annotation|@
name|Assisted
name|String
name|name
parameter_list|,
annotation|@
name|Assisted
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|name
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|Collator
name|collator
decl_stmt|;
name|String
name|rules
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"rules"
argument_list|)
decl_stmt|;
if|if
condition|(
name|rules
operator|!=
literal|null
condition|)
block|{
name|Exception
name|failureToResolve
init|=
literal|null
decl_stmt|;
try|try
block|{
name|rules
operator|=
name|Streams
operator|.
name|copyToString
argument_list|(
name|Files
operator|.
name|newBufferedReader
argument_list|(
name|environment
operator|.
name|configFile
argument_list|()
operator|.
name|resolve
argument_list|(
name|rules
argument_list|)
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|SecurityException
name|e
parameter_list|)
block|{
name|failureToResolve
operator|=
name|e
expr_stmt|;
block|}
try|try
block|{
name|collator
operator|=
operator|new
name|RuleBasedCollator
argument_list|(
name|rules
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|failureToResolve
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Failed to resolve collation rules location"
argument_list|,
name|failureToResolve
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Failed to parse collation rules"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
name|String
name|language
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"language"
argument_list|)
decl_stmt|;
if|if
condition|(
name|language
operator|!=
literal|null
condition|)
block|{
name|ULocale
name|locale
decl_stmt|;
name|String
name|country
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"country"
argument_list|)
decl_stmt|;
if|if
condition|(
name|country
operator|!=
literal|null
condition|)
block|{
name|String
name|variant
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"variant"
argument_list|)
decl_stmt|;
if|if
condition|(
name|variant
operator|!=
literal|null
condition|)
block|{
name|locale
operator|=
operator|new
name|ULocale
argument_list|(
name|language
argument_list|,
name|country
argument_list|,
name|variant
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|locale
operator|=
operator|new
name|ULocale
argument_list|(
name|language
argument_list|,
name|country
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|locale
operator|=
operator|new
name|ULocale
argument_list|(
name|language
argument_list|)
expr_stmt|;
block|}
name|collator
operator|=
name|Collator
operator|.
name|getInstance
argument_list|(
name|locale
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collator
operator|=
name|Collator
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
block|}
comment|// set the strength flag, otherwise it will be the default.
name|String
name|strength
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"strength"
argument_list|)
decl_stmt|;
if|if
condition|(
name|strength
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|strength
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"primary"
argument_list|)
condition|)
block|{
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|PRIMARY
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|strength
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"secondary"
argument_list|)
condition|)
block|{
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|SECONDARY
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|strength
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"tertiary"
argument_list|)
condition|)
block|{
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|TERTIARY
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|strength
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"quaternary"
argument_list|)
condition|)
block|{
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|QUATERNARY
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|strength
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"identical"
argument_list|)
condition|)
block|{
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|IDENTICAL
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid strength: "
operator|+
name|strength
argument_list|)
throw|;
block|}
block|}
comment|// set the decomposition flag, otherwise it will be the default.
name|String
name|decomposition
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"decomposition"
argument_list|)
decl_stmt|;
if|if
condition|(
name|decomposition
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|decomposition
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"no"
argument_list|)
condition|)
block|{
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|NO_DECOMPOSITION
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|decomposition
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"canonical"
argument_list|)
condition|)
block|{
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|CANONICAL_DECOMPOSITION
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid decomposition: "
operator|+
name|decomposition
argument_list|)
throw|;
block|}
block|}
comment|// expert options: concrete subclasses are always a RuleBasedCollator
name|RuleBasedCollator
name|rbc
init|=
operator|(
name|RuleBasedCollator
operator|)
name|collator
decl_stmt|;
name|String
name|alternate
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"alternate"
argument_list|)
decl_stmt|;
if|if
condition|(
name|alternate
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|alternate
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"shifted"
argument_list|)
condition|)
block|{
name|rbc
operator|.
name|setAlternateHandlingShifted
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|alternate
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"non-ignorable"
argument_list|)
condition|)
block|{
name|rbc
operator|.
name|setAlternateHandlingShifted
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid alternate: "
operator|+
name|alternate
argument_list|)
throw|;
block|}
block|}
name|Boolean
name|caseLevel
init|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"caseLevel"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|caseLevel
operator|!=
literal|null
condition|)
block|{
name|rbc
operator|.
name|setCaseLevel
argument_list|(
name|caseLevel
argument_list|)
expr_stmt|;
block|}
name|String
name|caseFirst
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"caseFirst"
argument_list|)
decl_stmt|;
if|if
condition|(
name|caseFirst
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|caseFirst
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"lower"
argument_list|)
condition|)
block|{
name|rbc
operator|.
name|setLowerCaseFirst
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|caseFirst
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"upper"
argument_list|)
condition|)
block|{
name|rbc
operator|.
name|setUpperCaseFirst
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid caseFirst: "
operator|+
name|caseFirst
argument_list|)
throw|;
block|}
block|}
name|Boolean
name|numeric
init|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"numeric"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|numeric
operator|!=
literal|null
condition|)
block|{
name|rbc
operator|.
name|setNumericCollation
argument_list|(
name|numeric
argument_list|)
expr_stmt|;
block|}
name|String
name|variableTop
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"variableTop"
argument_list|)
decl_stmt|;
if|if
condition|(
name|variableTop
operator|!=
literal|null
condition|)
block|{
name|rbc
operator|.
name|setVariableTop
argument_list|(
name|variableTop
argument_list|)
expr_stmt|;
block|}
name|Boolean
name|hiraganaQuaternaryMode
init|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"hiraganaQuaternaryMode"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|hiraganaQuaternaryMode
operator|!=
literal|null
condition|)
block|{
name|rbc
operator|.
name|setHiraganaQuaternary
argument_list|(
name|hiraganaQuaternaryMode
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|collator
operator|=
name|collator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
block|{
return|return
operator|new
name|ICUCollationKeyFilter
argument_list|(
name|tokenStream
argument_list|,
name|collator
argument_list|)
return|;
block|}
block|}
end_class

end_unit

