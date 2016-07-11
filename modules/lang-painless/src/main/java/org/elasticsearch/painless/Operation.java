begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.painless
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
package|;
end_package

begin_comment
comment|/**  * Provides a way to represent operations independently of ASM, to keep ASM  * contained to only the writing phase of compilation.  Note there are also  * a few extra operations not in ASM that are used internally by the  * Painless tree.  */
end_comment

begin_enum
DECL|enum|Operation
specifier|public
enum|enum
name|Operation
block|{
DECL|enum constant|MUL
name|MUL
argument_list|(
literal|"+"
argument_list|)
block|,
DECL|enum constant|DIV
name|DIV
argument_list|(
literal|"/"
argument_list|)
block|,
DECL|enum constant|REM
name|REM
argument_list|(
literal|"%"
argument_list|)
block|,
DECL|enum constant|ADD
name|ADD
argument_list|(
literal|"+"
argument_list|)
block|,
DECL|enum constant|SUB
name|SUB
argument_list|(
literal|"-"
argument_list|)
block|,
DECL|enum constant|FIND
name|FIND
argument_list|(
literal|"=~"
argument_list|)
block|,
DECL|enum constant|MATCH
name|MATCH
argument_list|(
literal|"==~"
argument_list|)
block|,
DECL|enum constant|LSH
name|LSH
argument_list|(
literal|"<<"
argument_list|)
block|,
DECL|enum constant|RSH
name|RSH
argument_list|(
literal|">>"
argument_list|)
block|,
DECL|enum constant|USH
name|USH
argument_list|(
literal|">>>"
argument_list|)
block|,
DECL|enum constant|BWNOT
name|BWNOT
argument_list|(
literal|"~"
argument_list|)
block|,
DECL|enum constant|BWAND
name|BWAND
argument_list|(
literal|"&"
argument_list|)
block|,
DECL|enum constant|XOR
name|XOR
argument_list|(
literal|"^"
argument_list|)
block|,
DECL|enum constant|BWOR
name|BWOR
argument_list|(
literal|"|"
argument_list|)
block|,
DECL|enum constant|NOT
name|NOT
argument_list|(
literal|"!"
argument_list|)
block|,
DECL|enum constant|AND
name|AND
argument_list|(
literal|"&&"
argument_list|)
block|,
DECL|enum constant|OR
name|OR
argument_list|(
literal|"||"
argument_list|)
block|,
DECL|enum constant|LT
name|LT
argument_list|(
literal|"<"
argument_list|)
block|,
DECL|enum constant|LTE
name|LTE
argument_list|(
literal|"<="
argument_list|)
block|,
DECL|enum constant|GT
name|GT
argument_list|(
literal|">"
argument_list|)
block|,
DECL|enum constant|GTE
name|GTE
argument_list|(
literal|">="
argument_list|)
block|,
DECL|enum constant|EQ
name|EQ
argument_list|(
literal|"=="
argument_list|)
block|,
DECL|enum constant|EQR
name|EQR
argument_list|(
literal|"==="
argument_list|)
block|,
DECL|enum constant|NE
name|NE
argument_list|(
literal|"!="
argument_list|)
block|,
DECL|enum constant|NER
name|NER
argument_list|(
literal|"!=="
argument_list|)
block|,
DECL|enum constant|INCR
name|INCR
argument_list|(
literal|"++"
argument_list|)
block|,
DECL|enum constant|DECR
name|DECR
argument_list|(
literal|"--"
argument_list|)
block|;
DECL|field|symbol
specifier|public
specifier|final
name|String
name|symbol
decl_stmt|;
DECL|method|Operation
name|Operation
parameter_list|(
specifier|final
name|String
name|symbol
parameter_list|)
block|{
name|this
operator|.
name|symbol
operator|=
name|symbol
expr_stmt|;
block|}
block|}
end_enum

end_unit

