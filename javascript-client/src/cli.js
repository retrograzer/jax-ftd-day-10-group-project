import net from 'net'
import vorpal from 'vorpal'

const cli = vorpal()

// cli config
cli
  .delimiter('>')

// connect mode
let server
let username = 'Unnamed User'

cli
  .command('setname <username>')
  .action(function (args, callback) {
    username = args['username']
    this.log(`Successfully changed username to ${username}.`)
    callback()
  })

cli
  .mode('connect <port> [host]')
  .delimiter('>')
  .init(function (args, callback) {
    server = net.createConnection(args, () => {
      const address = server.address()
      this.log(`Connected to server ${address.address}:${address.port}`)
      callback()
    })

    function getDate () {
      let date = new Date()
      let ampm = date.getHours() < 12
        ? 'AM'
        : 'PM'
      let hours = date.getHours() % 12
        ? date.getHours() % 12
        : 12
      let minutes = date.getMinutes() < 10
        ? '0' + date.getMinutes()
        : date.getMinutes()
      let seconds = date.getSeconds() < 10
        ? '0' + date.getSeconds()
        : date.getSeconds()
      let datestring = `${date.getMonth()}/${date.getDate()}/${date.getFullYear()}, ${hours}:${minutes}:${seconds} ${ampm}`
      return datestring
    }

    server.on('data', (data) => {
      this.log(`${getDate()} - ${username}: ${data.toString()}`)
    })

    server.on('end', () => {
      this.log('Disconnected from server.')
    })
  })
  .action(function (command, callback) {
    if (command === 'exit') {
      server.end()
      callback()
    } else {
      server.write(command + '\n')
      callback()
    }
  })

export default cli
