import net from 'net'
import vorpal from 'vorpal'



const cli = vorpal()

// cli config
cli
  .delimiter('ftd-chat~$')

// connect mode
let server


cli
  .mode('connect [host] <port>')
  .delimiter('connected:')
  .init(function (args, callback) {
    server = net.createConnection(args, () => {
      const address = server.address()
      this.log(`connected to server ${address.address}:${address.port}`)
      callback()
    })

    server.on('data', (data) => {
      this.log(data.toString())
    })

    server.on('end', () => {
      this.log('disconnected from server :(')
    })
  })
  .action(function (command, callback) {
    if (command === 'exit') {
      server.end()
      callback()
    } else if (command === 'username') {
      callback()
    } else {
      server.write(command + '\n')
      callback()
    }
  })

export default cli
