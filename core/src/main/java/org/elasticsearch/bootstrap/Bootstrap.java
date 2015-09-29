begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.bootstrap
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|bootstrap
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
name|Constants
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
name|StringHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|PidFile
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
name|SuppressForbidden
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
name|cli
operator|.
name|CliTool
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
name|cli
operator|.
name|Terminal
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
name|collect
operator|.
name|Tuple
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
name|CreationException
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
name|lease
operator|.
name|Releasables
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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
name|logging
operator|.
name|log4j
operator|.
name|LogConfigurator
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
name|monitor
operator|.
name|jvm
operator|.
name|JvmInfo
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
name|os
operator|.
name|OsProbe
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
name|process
operator|.
name|ProcessProbe
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|NodeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|internal
operator|.
name|InternalSettingsPreparer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
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
name|concurrent
operator|.
name|CountDownLatch
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
name|settings
operator|.
name|Settings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
import|;
end_import

begin_comment
comment|/**  * Internal startup code.  */
end_comment

begin_class
DECL|class|Bootstrap
specifier|final
class|class
name|Bootstrap
block|{
DECL|field|INSTANCE
specifier|private
specifier|static
specifier|volatile
name|Bootstrap
name|INSTANCE
decl_stmt|;
DECL|field|node
specifier|private
specifier|volatile
name|Node
name|node
decl_stmt|;
DECL|field|keepAliveLatch
specifier|private
specifier|final
name|CountDownLatch
name|keepAliveLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|keepAliveThread
specifier|private
specifier|final
name|Thread
name|keepAliveThread
decl_stmt|;
comment|/** creates a new instance */
DECL|method|Bootstrap
name|Bootstrap
parameter_list|()
block|{
name|keepAliveThread
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|keepAliveLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// bail out
block|}
block|}
block|}
argument_list|,
literal|"elasticsearch[keepAlive/"
operator|+
name|Version
operator|.
name|CURRENT
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|keepAliveThread
operator|.
name|setDaemon
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// keep this thread alive (non daemon thread) until we shutdown
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|keepAliveLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** initialize native resources */
DECL|method|initializeNatives
specifier|public
specifier|static
name|void
name|initializeNatives
parameter_list|(
name|boolean
name|mlockAll
parameter_list|,
name|boolean
name|seccomp
parameter_list|,
name|boolean
name|ctrlHandler
parameter_list|)
block|{
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|Bootstrap
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// check if the user is running as root, and bail
if|if
condition|(
name|Natives
operator|.
name|definitelyRunningAsRoot
argument_list|()
condition|)
block|{
if|if
condition|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"es.insecure.allow.root"
argument_list|)
argument_list|)
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"running as ROOT user. this is a bad idea!"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"don't run elasticsearch as root."
argument_list|)
throw|;
block|}
block|}
comment|// enable secure computing mode
if|if
condition|(
name|seccomp
condition|)
block|{
name|Natives
operator|.
name|trySeccomp
argument_list|()
expr_stmt|;
block|}
comment|// mlockall if requested
if|if
condition|(
name|mlockAll
condition|)
block|{
if|if
condition|(
name|Constants
operator|.
name|WINDOWS
condition|)
block|{
name|Natives
operator|.
name|tryVirtualLock
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Natives
operator|.
name|tryMlockall
argument_list|()
expr_stmt|;
block|}
block|}
comment|// listener for windows close event
if|if
condition|(
name|ctrlHandler
condition|)
block|{
name|Natives
operator|.
name|addConsoleCtrlHandler
argument_list|(
operator|new
name|ConsoleCtrlHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|handle
parameter_list|(
name|int
name|code
parameter_list|)
block|{
if|if
condition|(
name|CTRL_CLOSE_EVENT
operator|==
name|code
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"running graceful exit on windows"
argument_list|)
expr_stmt|;
name|Bootstrap
operator|.
name|stop
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|// force remainder of JNA to be loaded (if available).
try|try
block|{
name|JNAKernel32Library
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignored
parameter_list|)
block|{
comment|// we've already logged this.
block|}
comment|// init lucene random seed. it will use /dev/urandom where available:
name|StringHelper
operator|.
name|randomId
argument_list|()
expr_stmt|;
block|}
DECL|method|initializeProbes
specifier|static
name|void
name|initializeProbes
parameter_list|()
block|{
comment|// Force probes to be loaded
name|ProcessProbe
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|OsProbe
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
DECL|method|setup
specifier|private
name|void
name|setup
parameter_list|(
name|boolean
name|addShutdownHook
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|Environment
name|environment
parameter_list|)
throws|throws
name|Exception
block|{
name|initializeNatives
argument_list|(
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"bootstrap.mlockall"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"bootstrap.seccomp"
argument_list|,
literal|true
argument_list|)
argument_list|,
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"bootstrap.ctrlhandler"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// initialize probes before the security manager is installed
name|initializeProbes
argument_list|()
expr_stmt|;
if|if
condition|(
name|addShutdownHook
condition|)
block|{
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|node
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|// look for jar hell
name|JarHell
operator|.
name|checkJarHell
argument_list|()
expr_stmt|;
comment|// install SM after natives, shutdown hooks, etc.
name|setupSecurity
argument_list|(
name|settings
argument_list|,
name|environment
argument_list|)
expr_stmt|;
comment|// We do not need to reload system properties here as we have already applied them in building the settings and
comment|// reloading could cause multiple prompts to the user for values if a system property was specified with a prompt
comment|// placeholder
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|settings
argument_list|)
operator|.
name|put
argument_list|(
name|InternalSettingsPreparer
operator|.
name|IGNORE_SYSTEM_PROPERTIES_SETTING
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NodeBuilder
name|nodeBuilder
init|=
name|NodeBuilder
operator|.
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|nodeSettings
argument_list|)
decl_stmt|;
name|node
operator|=
name|nodeBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|/**       * option for elasticsearch.yml etc to turn off our security manager completely,      * for example if you want to have your own configuration or just disable.      */
DECL|field|SECURITY_SETTING
specifier|static
specifier|final
name|String
name|SECURITY_SETTING
init|=
literal|"security.manager.enabled"
decl_stmt|;
DECL|method|setupSecurity
specifier|private
name|void
name|setupSecurity
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Environment
name|environment
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|SECURITY_SETTING
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|Security
operator|.
name|configure
argument_list|(
name|environment
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Exception#printStackTrace()"
argument_list|)
DECL|method|setupLogging
specifier|private
specifier|static
name|void
name|setupLogging
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Environment
name|environment
parameter_list|)
block|{
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.log4j.Logger"
argument_list|)
expr_stmt|;
name|LogConfigurator
operator|.
name|configure
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
comment|// no log4j
block|}
catch|catch
parameter_list|(
name|NoClassDefFoundError
name|e
parameter_list|)
block|{
comment|// no log4j
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|sysError
argument_list|(
literal|"Failed to configure logging..."
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|initialSettings
specifier|private
specifier|static
name|Environment
name|initialSettings
parameter_list|(
name|boolean
name|foreground
parameter_list|)
block|{
name|Terminal
name|terminal
init|=
name|foreground
condition|?
name|Terminal
operator|.
name|DEFAULT
else|:
literal|null
decl_stmt|;
return|return
name|InternalSettingsPreparer
operator|.
name|prepareEnvironment
argument_list|(
name|EMPTY_SETTINGS
argument_list|,
name|terminal
argument_list|)
return|;
block|}
DECL|method|start
specifier|private
name|void
name|start
parameter_list|()
block|{
name|node
operator|.
name|start
argument_list|()
expr_stmt|;
name|keepAliveThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|stop
specifier|static
name|void
name|stop
parameter_list|()
block|{
try|try
block|{
name|Releasables
operator|.
name|close
argument_list|(
name|INSTANCE
operator|.
name|node
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|INSTANCE
operator|.
name|keepAliveLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * This method is invoked by {@link Elasticsearch#main(String[])}      * to startup elasticsearch.      */
DECL|method|init
specifier|static
name|void
name|init
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
comment|// Set the system property before anything has a chance to trigger its use
name|System
operator|.
name|setProperty
argument_list|(
literal|"es.logger.prefix"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|BootstrapCLIParser
name|bootstrapCLIParser
init|=
operator|new
name|BootstrapCLIParser
argument_list|()
decl_stmt|;
name|CliTool
operator|.
name|ExitStatus
name|status
init|=
name|bootstrapCLIParser
operator|.
name|execute
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|CliTool
operator|.
name|ExitStatus
operator|.
name|OK
operator|!=
name|status
condition|)
block|{
name|System
operator|.
name|exit
argument_list|(
name|status
operator|.
name|status
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|INSTANCE
operator|=
operator|new
name|Bootstrap
argument_list|()
expr_stmt|;
name|boolean
name|foreground
init|=
operator|!
literal|"false"
operator|.
name|equals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"es.foreground"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"es-foreground"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// handle the wrapper system property, if its a service, don't run as a service
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"wrapper.service"
argument_list|,
literal|"XXX"
argument_list|)
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
condition|)
block|{
name|foreground
operator|=
literal|false
expr_stmt|;
block|}
name|Environment
name|environment
init|=
name|initialSettings
argument_list|(
name|foreground
argument_list|)
decl_stmt|;
name|Settings
name|settings
init|=
name|environment
operator|.
name|settings
argument_list|()
decl_stmt|;
if|if
condition|(
name|environment
operator|.
name|pidFile
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|PidFile
operator|.
name|create
argument_list|(
name|environment
operator|.
name|pidFile
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|setupLogging
argument_list|(
name|settings
argument_list|,
name|environment
argument_list|)
expr_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"es.max-open-files"
argument_list|,
literal|"false"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
condition|)
block|{
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|Bootstrap
operator|.
name|class
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"max_open_files [{}]"
argument_list|,
name|ProcessProbe
operator|.
name|getInstance
argument_list|()
operator|.
name|getMaxFileDescriptorCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// warn if running using the client VM
if|if
condition|(
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|getVmName
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|contains
argument_list|(
literal|"client"
argument_list|)
condition|)
block|{
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|Bootstrap
operator|.
name|class
argument_list|)
decl_stmt|;
name|logger
operator|.
name|warn
argument_list|(
literal|"jvm uses the client vm, make sure to run `java` with the server vm for best performance by adding `-server` to the command line"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
operator|!
name|foreground
condition|)
block|{
name|Loggers
operator|.
name|disableConsoleLogging
argument_list|()
expr_stmt|;
name|closeSystOut
argument_list|()
expr_stmt|;
block|}
comment|// fail if using broken version
name|JVMCheck
operator|.
name|check
argument_list|()
expr_stmt|;
name|INSTANCE
operator|.
name|setup
argument_list|(
literal|true
argument_list|,
name|settings
argument_list|,
name|environment
argument_list|)
expr_stmt|;
name|INSTANCE
operator|.
name|start
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|foreground
condition|)
block|{
name|closeSysError
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// disable console logging, so user does not see the exception twice (jvm will show it already)
if|if
condition|(
name|foreground
condition|)
block|{
name|Loggers
operator|.
name|disableConsoleLogging
argument_list|()
expr_stmt|;
block|}
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|Bootstrap
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|INSTANCE
operator|.
name|node
operator|!=
literal|null
condition|)
block|{
name|logger
operator|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|Bootstrap
operator|.
name|class
argument_list|,
name|INSTANCE
operator|.
name|node
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// HACK, it sucks to do this, but we will run users out of disk space otherwise
if|if
condition|(
name|e
operator|instanceof
name|CreationException
condition|)
block|{
comment|// guice: log the shortened exc to the log file
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|ps
init|=
operator|new
name|PrintStream
argument_list|(
name|os
argument_list|,
literal|false
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
operator|new
name|StartupError
argument_list|(
name|e
argument_list|)
operator|.
name|printStackTrace
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|ps
operator|.
name|flush
argument_list|()
expr_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"Guice Exception: {}"
argument_list|,
name|os
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// full exception
name|logger
operator|.
name|error
argument_list|(
literal|"Exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// re-enable it if appropriate, so they can see any logging during the shutdown process
if|if
condition|(
name|foreground
condition|)
block|{
name|Loggers
operator|.
name|enableConsoleLogging
argument_list|()
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"System#out"
argument_list|)
DECL|method|closeSystOut
specifier|private
specifier|static
name|void
name|closeSystOut
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"System#err"
argument_list|)
DECL|method|closeSysError
specifier|private
specifier|static
name|void
name|closeSysError
parameter_list|()
block|{
name|System
operator|.
name|err
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"System#err"
argument_list|)
DECL|method|sysError
specifier|private
specifier|static
name|void
name|sysError
parameter_list|(
name|String
name|line
parameter_list|,
name|boolean
name|flush
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|line
argument_list|)
expr_stmt|;
if|if
condition|(
name|flush
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

