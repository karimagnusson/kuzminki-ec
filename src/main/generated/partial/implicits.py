#! /usr/bin/env python


java_ref_types = [
    'String',
    'Boolean',
    'Short',
    'Int',
    'Long',
    'Float',
    'Double',
    'BigDecimal',
    'Time',
    'Date',
    'Timestamp'
]

impl_ref_1 = '  implicit val kzimpl%sCol: ColInfo => TypeCol[%s] = info => %sModelCol(info)\n'
impl_seq_1 = '  implicit val kzimpl%sSeqCol: ColInfo => TypeCol[Seq[%s]] = info => %sSeqModelCol(info)\n'

impl_ref_2 = '  implicit val kzimplTo%sModelCol: TypeCol[%s] => %sModelCol = col => col.asInstanceOf[%sModelCol]\n'
impl_seq_2 = '  implicit val kzimplTo%sSeqModelCol: TypeCol[Seq[%s]] => %sSeqModelCol = col => col.asInstanceOf[%sSeqModelCol]\n'

impl_ref_3 = '  implicit val kzimplTypeColTo%sCol: TypeCol[%s] => %sCol = col => col.asInstanceOf[%sCol]\n'
impl_seq_3 = '  implicit val kzimplTypeColTo%sSeqCol: TypeCol[Seq[%s]] => %sSeqCol = col => col.asInstanceOf[%sSeqCol]\n'

parts = []

parts.extend(['\n', '  // create model col\n', '\n'])

for name in java_ref_types:
    parts.append(impl_ref_1 % (name, name, name))

parts.append('\n')

for name in java_ref_types:
    parts.append(impl_seq_1 % (name, name, name))

parts.extend(['\n', '  // model type col\n', '\n'])

for name in java_ref_types:
    parts.append(impl_ref_2 % (name, name, name, name))

parts.append('\n')

for name in java_ref_types:
    parts.append(impl_seq_2 % (name, name, name, name))

parts.extend(['\n', '  // type col\n', '\n'])

for name in java_ref_types:
    parts.append(impl_ref_3 % (name, name, name, name))

parts.append('\n')

for name in java_ref_types:
    parts.append(impl_seq_3 % (name, name, name, name))

parts.append('\n')

content = ''.join(parts)

f = open('./output/implicits.txt', 'w')
f.write(content)
f.close()









