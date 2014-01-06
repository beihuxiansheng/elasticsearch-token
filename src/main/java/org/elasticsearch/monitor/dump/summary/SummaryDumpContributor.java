begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor.dump.summary
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|dump
operator|.
name|summary
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
name|monitor
operator|.
name|dump
operator|.
name|Dump
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
name|dump
operator|.
name|DumpContributionFailedException
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
name|dump
operator|.
name|DumpContributor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SummaryDumpContributor
specifier|public
class|class
name|SummaryDumpContributor
implements|implements
name|DumpContributor
block|{
DECL|field|dateFormat
specifier|private
specifier|final
name|DateFormat
name|dateFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm:ss,SSS"
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
DECL|field|formatterLock
specifier|private
specifier|final
name|Object
name|formatterLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|SUMMARY
specifier|public
specifier|static
specifier|final
name|String
name|SUMMARY
init|=
literal|"summary"
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
annotation|@
name|Inject
DECL|method|SummaryDumpContributor
specifier|public
name|SummaryDumpContributor
parameter_list|(
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
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|contribute
specifier|public
name|void
name|contribute
parameter_list|(
name|Dump
name|dump
parameter_list|)
throws|throws
name|DumpContributionFailedException
block|{
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|dump
operator|.
name|createFileWriter
argument_list|(
literal|"summary.txt"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|processHeader
argument_list|(
name|writer
argument_list|,
name|dump
operator|.
name|timestamp
argument_list|()
argument_list|)
expr_stmt|;
name|processCause
argument_list|(
name|writer
argument_list|,
name|dump
operator|.
name|cause
argument_list|()
argument_list|)
expr_stmt|;
name|processThrowables
argument_list|(
name|writer
argument_list|,
name|dump
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DumpContributionFailedException
argument_list|(
name|getName
argument_list|()
argument_list|,
literal|"Failed to generate"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
DECL|method|processHeader
specifier|private
name|void
name|processHeader
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
synchronized|synchronized
init|(
name|formatterLock
init|)
block|{
name|writer
operator|.
name|println
argument_list|(
literal|"===== TIME ====="
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
name|dateFormat
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|timestamp
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|processCause
specifier|private
name|void
name|processCause
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|String
name|cause
parameter_list|)
block|{
name|writer
operator|.
name|println
argument_list|(
literal|"===== CAUSE ====="
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
DECL|method|processThrowables
specifier|private
name|void
name|processThrowables
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|Dump
name|dump
parameter_list|)
block|{
name|writer
operator|.
name|println
argument_list|(
literal|"===== EXCEPTIONS ====="
argument_list|)
expr_stmt|;
name|Object
name|throwables
init|=
name|dump
operator|.
name|context
argument_list|()
operator|.
name|get
argument_list|(
literal|"throwables"
argument_list|)
decl_stmt|;
if|if
condition|(
name|throwables
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|throwables
operator|instanceof
name|Throwable
index|[]
condition|)
block|{
name|Throwable
index|[]
name|array
init|=
operator|(
name|Throwable
index|[]
operator|)
name|throwables
decl_stmt|;
for|for
control|(
name|Throwable
name|t
range|:
name|array
control|)
block|{
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"---- Exception ----"
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|throwables
operator|instanceof
name|Collection
condition|)
block|{
name|Collection
name|collection
init|=
operator|(
name|Collection
operator|)
name|throwables
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|collection
control|)
block|{
name|Throwable
name|t
init|=
operator|(
name|Throwable
operator|)
name|o
decl_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"---- Exception ----"
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|DumpContributionFailedException
argument_list|(
name|getName
argument_list|()
argument_list|,
literal|"Can't handle throwables type ["
operator|+
name|throwables
operator|.
name|getClass
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

