begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.grok
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|grok
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|core
operator|.
name|IngestDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|core
operator|.
name|ConfigurationUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|core
operator|.
name|Processor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|Collections
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
name|Map
import|;
end_import

begin_class
DECL|class|GrokProcessor
specifier|public
specifier|final
class|class
name|GrokProcessor
implements|implements
name|Processor
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"grok"
decl_stmt|;
DECL|field|matchField
specifier|private
specifier|final
name|String
name|matchField
decl_stmt|;
DECL|field|grok
specifier|private
specifier|final
name|Grok
name|grok
decl_stmt|;
DECL|method|GrokProcessor
specifier|public
name|GrokProcessor
parameter_list|(
name|Grok
name|grok
parameter_list|,
name|String
name|matchField
parameter_list|)
block|{
name|this
operator|.
name|matchField
operator|=
name|matchField
expr_stmt|;
name|this
operator|.
name|grok
operator|=
name|grok
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|IngestDocument
name|ingestDocument
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|fieldValue
init|=
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
name|matchField
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|matches
init|=
name|grok
operator|.
name|captures
argument_list|(
name|fieldValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|matches
operator|!=
literal|null
condition|)
block|{
name|matches
operator|.
name|forEach
argument_list|(
parameter_list|(
name|k
parameter_list|,
name|v
parameter_list|)
lambda|->
name|ingestDocument
operator|.
name|setFieldValue
argument_list|(
name|k
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Grok expression does not match field value: ["
operator|+
name|fieldValue
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
DECL|method|getMatchField
name|String
name|getMatchField
parameter_list|()
block|{
return|return
name|matchField
return|;
block|}
DECL|method|getGrok
name|Grok
name|getGrok
parameter_list|()
block|{
return|return
name|grok
return|;
block|}
DECL|class|Factory
specifier|public
specifier|final
specifier|static
class|class
name|Factory
implements|implements
name|Processor
operator|.
name|Factory
argument_list|<
name|GrokProcessor
argument_list|>
block|{
DECL|field|PATTERN_NAMES
specifier|private
specifier|final
specifier|static
name|String
index|[]
name|PATTERN_NAMES
init|=
operator|new
name|String
index|[]
block|{
literal|"aws"
block|,
literal|"bacula"
block|,
literal|"bro"
block|,
literal|"exim"
block|,
literal|"firewalls"
block|,
literal|"grok-patterns"
block|,
literal|"haproxy"
block|,
literal|"java"
block|,
literal|"junos"
block|,
literal|"linux-syslog"
block|,
literal|"mcollective-patterns"
block|,
literal|"mongodb"
block|,
literal|"nagios"
block|,
literal|"postgresql"
block|,
literal|"rails"
block|,
literal|"redis"
block|,
literal|"ruby"
block|}
decl_stmt|;
DECL|field|builtinPatternBank
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|builtinPatternBank
decl_stmt|;
DECL|method|Factory
specifier|public
name|Factory
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO(simonw): we should have a static helper method to load these patterns and make this
comment|// factory only accept a String->String map instead. That way we can load
comment|// the patterns in the IngestGrokPlugin ctor or even in a static context and this ctor doesn't need to throw any exception.
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|builtinPatterns
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|pattern
range|:
name|PATTERN_NAMES
control|)
block|{
try|try
init|(
name|InputStream
name|is
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/patterns/"
operator|+
name|pattern
argument_list|)
init|)
block|{
name|loadBankFromStream
argument_list|(
name|builtinPatterns
argument_list|,
name|is
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|builtinPatternBank
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|builtinPatterns
argument_list|)
expr_stmt|;
block|}
DECL|method|loadBankFromStream
specifier|static
name|void
name|loadBankFromStream
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|patternBank
parameter_list|,
name|InputStream
name|inputStream
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|line
decl_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|inputStream
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|trimmedLine
init|=
name|line
operator|.
name|replaceAll
argument_list|(
literal|"^\\s+"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|trimmedLine
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
operator|||
name|trimmedLine
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|String
index|[]
name|parts
init|=
name|trimmedLine
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|patternBank
operator|.
name|put
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|,
name|parts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|create
specifier|public
name|GrokProcessor
name|create
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|matchField
init|=
name|ConfigurationUtils
operator|.
name|readStringProperty
argument_list|(
name|config
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
name|String
name|matchPattern
init|=
name|ConfigurationUtils
operator|.
name|readStringProperty
argument_list|(
name|config
argument_list|,
literal|"pattern"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|customPatternBank
init|=
name|ConfigurationUtils
operator|.
name|readOptionalMap
argument_list|(
name|config
argument_list|,
literal|"pattern_definitions"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|patternBank
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|builtinPatternBank
argument_list|)
decl_stmt|;
if|if
condition|(
name|customPatternBank
operator|!=
literal|null
condition|)
block|{
name|patternBank
operator|.
name|putAll
argument_list|(
name|customPatternBank
argument_list|)
expr_stmt|;
block|}
name|Grok
name|grok
init|=
operator|new
name|Grok
argument_list|(
name|patternBank
argument_list|,
name|matchPattern
argument_list|)
decl_stmt|;
return|return
operator|new
name|GrokProcessor
argument_list|(
name|grok
argument_list|,
name|matchField
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

