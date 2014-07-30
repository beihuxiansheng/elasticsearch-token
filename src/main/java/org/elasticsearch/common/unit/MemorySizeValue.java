begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.unit
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
operator|.
name|JvmInfo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
operator|.
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
import|;
end_import

begin_comment
comment|/** Utility methods to get memory sizes. */
end_comment

begin_enum
DECL|enum|MemorySizeValue
specifier|public
enum|enum
name|MemorySizeValue
block|{     ;
comment|/** Parse the provided string as a memory size. This method either accepts absolute values such as      *<tt>42</tt> (default assumed unit is byte) or<tt>2mb</tt>, or percentages of the heap size: if      *  the heap is 1G,<tt>10%</tt> will be parsed as<tt>100mb</tt>.  */
DECL|method|parseBytesSizeValueOrHeapRatio
specifier|public
specifier|static
name|ByteSizeValue
name|parseBytesSizeValueOrHeapRatio
parameter_list|(
name|String
name|sValue
parameter_list|)
block|{
if|if
condition|(
name|sValue
operator|!=
literal|null
operator|&&
name|sValue
operator|.
name|endsWith
argument_list|(
literal|"%"
argument_list|)
condition|)
block|{
specifier|final
name|String
name|percentAsString
init|=
name|sValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|double
name|percent
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|percentAsString
argument_list|)
decl_stmt|;
if|if
condition|(
name|percent
argument_list|<
literal|0
operator|||
name|percent
argument_list|>
literal|100
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Percentage should be in [0-100], got "
operator|+
name|percentAsString
argument_list|)
throw|;
block|}
return|return
operator|new
name|ByteSizeValue
argument_list|(
call|(
name|long
call|)
argument_list|(
operator|(
name|percent
operator|/
literal|100
operator|)
operator|*
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|getMem
argument_list|()
operator|.
name|getHeapMax
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|,
name|ByteSizeUnit
operator|.
name|BYTES
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed to parse ["
operator|+
name|percentAsString
operator|+
literal|"] as a double"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
return|return
name|parseBytesSizeValue
argument_list|(
name|sValue
argument_list|)
return|;
block|}
block|}
block|}
end_enum

end_unit

